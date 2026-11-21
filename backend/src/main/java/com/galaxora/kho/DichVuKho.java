package com.galaxora.kho;

import static com.galaxora.banhang.LoiBanHang.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.galaxora.quantri.NhatKyQuanTri;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DichVuKho {
    private final JdbcTemplate jdbc;
    private final SimpleJdbcInsert themPhieu;
    private final NhatKyQuanTri nhatKy;

    public DichVuKho(JdbcTemplate jdbc, NhatKyQuanTri nhatKy) {
        this.jdbc = jdbc;
        this.nhatKy = nhatKy;
        themPhieu = new SimpleJdbcInsert(jdbc).withTableName("phieu_nhap_kho")
            .usingColumns("nguoi_dung_id", "khoa_yeu_cau", "ly_do").usingGeneratedKeyColumns("id");
    }

    record TonKho(long sanPhamId, String sku, String ten, int thucTe, int dangGiu, int khaDung, String canhBao) {}
    record GiaoDich(long id, String loai, int thayDoiThucTe, int thayDoiDangGiu, int thucTeSau, int dangGiuSau,
                     String lyDo, Long phieuNhapId, Long donHangId, LocalDateTime thoiDiem) {}
    record Phieu(long id, String lyDo, LocalDateTime thoiDiem) {}
    record ChiTietPhieu(Phieu phieu, List<KhoController.DongNhap> chiTiet) {}

    List<TonKho> tonKho() {
        return jdbc.query("""
            SELECT s.id,s.sku,s.ten,k.so_luong_thuc_te,k.so_luong_dang_giu,
                   k.so_luong_thuc_te-k.so_luong_dang_giu AS kha_dung,
                   CASE WHEN k.so_luong_thuc_te=k.so_luong_dang_giu THEN 'HET_HANG'
                        WHEN k.so_luong_thuc_te-k.so_luong_dang_giu<=c.nguong_ton_kho THEN 'SAP_HET'
                        ELSE 'CON_HANG' END AS canh_bao
            FROM ton_kho k JOIN san_pham s ON s.id=k.san_pham_id CROSS JOIN cau_hinh_ban_hang c
            ORDER BY kha_dung,s.id
            """, (r, i) -> new TonKho(r.getLong(1), r.getString(2), r.getString(3), r.getInt(4), r.getInt(5),
                r.getInt(6), r.getString(7)));
    }

    Integer nguong() {
        return jdbc.queryForObject("SELECT nguong_ton_kho FROM cau_hinh_ban_hang WHERE id=1", Integer.class);
    }

    @Transactional
    void datNguong(long adminId, int giaTri) {
        jdbc.update("UPDATE cau_hinh_ban_hang SET nguong_ton_kho=? WHERE id=1", giaTri);
        nhatKy.ghiTrongGiaoDich(adminId, "Đổi ngưỡng tồn kho", "/api/quan-tri/kho/nguong");
    }

    List<GiaoDich> lichSu(long id) {
        return jdbc.query("""
            SELECT id,loai,thay_doi_thuc_te,thay_doi_dang_giu,thuc_te_sau,dang_giu_sau,ly_do,
                phieu_nhap_id,don_hang_id,created_at FROM giao_dich_kho WHERE san_pham_id=? ORDER BY id DESC
            """, (r, i) -> new GiaoDich(r.getLong(1), r.getString(2), r.getInt(3), r.getInt(4), r.getInt(5),
                r.getInt(6), r.getString(7), r.getObject(8, Long.class), r.getObject(9, Long.class),
                r.getObject(10, LocalDateTime.class)), id);
    }

    List<Phieu> phieuNhap() {
        return jdbc.query("SELECT id,ly_do,created_at FROM phieu_nhap_kho ORDER BY id DESC",
            (r, i) -> new Phieu(r.getLong(1), r.getString(2), r.getObject(3, LocalDateTime.class)));
    }

    ChiTietPhieu chiTietPhieu(long id) {
        var phieu = jdbc.query("SELECT id,ly_do,created_at FROM phieu_nhap_kho WHERE id=?",
            (r, i) -> new Phieu(r.getLong(1), r.getString(2), r.getObject(3, LocalDateTime.class)), id)
            .stream().findFirst().orElseThrow(() -> khongTimThay());
        return new ChiTietPhieu(phieu, jdbc.query("""
            SELECT san_pham_id,so_luong FROM chi_tiet_phieu_nhap WHERE phieu_nhap_id=? ORDER BY san_pham_id
            """, (r, i) -> new KhoController.DongNhap(r.getLong(1), r.getInt(2)), id));
    }

    @Transactional
    long nhap(long nguoiDungId, KhoController.NhapKho duLieu) {
        khoaNguoiThucHien(nguoiDungId);
        var daTao = jdbc.queryForList("SELECT id FROM phieu_nhap_kho WHERE khoa_yeu_cau=? AND nguoi_dung_id=?",
            Long.class, duLieu.khoaYeuCau(), nguoiDungId);
        if (!daTao.isEmpty()) return daTao.getFirst();
        var danhSachId = new HashSet<Long>();
        for (var dong : duLieu.chiTiet()) {
            if (!danhSachId.add(dong.sanPhamId())) throw khongHopLe("Một sản phẩm chỉ xuất hiện một lần trong phiếu.");
        }
        long id = themPhieu.executeAndReturnKey(Map.of("nguoi_dung_id", nguoiDungId, "khoa_yeu_cau",
            duLieu.khoaYeuCau(), "ly_do", duLieu.lyDo().strip())).longValue();
        for (var dong : duLieu.chiTiet().stream().sorted(Comparator.comparingLong(KhoController.DongNhap::sanPhamId)).toList()) {
            bienDong(dong.sanPhamId(), nguoiDungId, "NHAP", dong.soLuong(), 0, duLieu.lyDo(), id, null, null);
            jdbc.update("INSERT INTO chi_tiet_phieu_nhap (phieu_nhap_id,san_pham_id,so_luong) VALUES (?,?,?)",
                id, dong.sanPhamId(), dong.soLuong());
        }
        nhatKy.ghiTrongGiaoDich(nguoiDungId, "Nhập kho", "/api/quan-tri/kho/phieu-nhap/" + id);
        return id;
    }

    @Transactional
    void dieuChinh(long nguoiDungId, long sanPhamId, KhoController.DieuChinh duLieu) {
        khoaNguoiThucHien(nguoiDungId);
        var daCo = jdbc.queryForObject("SELECT COUNT(*) FROM giao_dich_kho WHERE khoa_yeu_cau=? AND nguoi_dung_id=?",
            Long.class, duLieu.khoaYeuCau(), nguoiDungId);
        if (daCo != null && daCo > 0) return;
        if (duLieu.thayDoi() == 0 || (duLieu.xuatKho() && duLieu.thayDoi() > 0)) {
            throw khongHopLe("Số điều chỉnh phải khác 0, xuất kho phải là số âm.");
        }
        bienDong(sanPhamId, nguoiDungId, duLieu.xuatKho() ? "XUAT" : "DIEU_CHINH",
            duLieu.thayDoi(), 0, duLieu.lyDo(), null, null, duLieu.khoaYeuCau());
        nhatKy.ghiTrongGiaoDich(nguoiDungId, duLieu.xuatKho() ? "Xuất kho" : "Điều chỉnh tồn kho",
            "/api/quan-tri/kho/ton/" + sanPhamId + "/dieu-chinh");
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
    public void giu(long sanPhamId, long nguoiDungId, long donHangId, int soLuong) {
        bienDong(sanPhamId, nguoiDungId, "GIU", 0, soLuong, "Xác nhận đơn hàng", null, donHangId, null);
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.MANDATORY)
    public void ketThucGiu(long sanPhamId, long nguoiDungId, long donHangId, int soLuong, boolean daBan) {
        var daGiu = jdbc.queryForObject("""
            SELECT COUNT(*) FROM giao_dich_kho
            WHERE san_pham_id=? AND don_hang_id=? AND loai='GIU' AND thay_doi_dang_giu=?
            """, Long.class, sanPhamId, donHangId, soLuong);
        if (daGiu == null || daGiu != 1) throw xungDot("Không tìm thấy lượng giữ của đơn.");
        bienDong(sanPhamId, nguoiDungId, daBan ? "XUAT_BAN" : "GIAI_PHONG", daBan ? -soLuong : 0, -soLuong,
            daBan ? "Giao hàng thành công" : "Kết thúc giữ hàng", null, donHangId, null);
    }

    private void khoaNguoiThucHien(long id) {
        jdbc.queryForObject("SELECT id FROM nguoi_dung WHERE id=? FOR UPDATE", Long.class, id);
    }

    private void bienDong(long sanPhamId, long nguoiDungId, String loai, int thucTe, int dangGiu,
        String lyDo, Long phieuNhapId, Long donHangId, String khoa) {
        var ton = jdbc.query("""
            SELECT so_luong_thuc_te,so_luong_dang_giu FROM ton_kho WHERE san_pham_id=? FOR UPDATE
            """, (r, i) -> new int[]{r.getInt(1), r.getInt(2)}, sanPhamId).stream().findFirst()
            .orElseThrow(() -> khongTimThay());
        long thucTeSau = (long) ton[0] + thucTe;
        long dangGiuSau = (long) ton[1] + dangGiu;
        if (dangGiuSau < 0 || thucTeSau < dangGiuSau || thucTeSau > Integer.MAX_VALUE) {
            throw xungDot("Không đủ tồn khả dụng hoặc biến động vượt giới hạn kho cho sản phẩm #" + sanPhamId + ".");
        }
        jdbc.update("UPDATE ton_kho SET so_luong_thuc_te=?,so_luong_dang_giu=? WHERE san_pham_id=?",
            thucTeSau, dangGiuSau, sanPhamId);
        jdbc.update("""
            INSERT INTO giao_dich_kho
              (san_pham_id,nguoi_dung_id,loai,thay_doi_thuc_te,thay_doi_dang_giu,thuc_te_sau,dang_giu_sau,
               ly_do,phieu_nhap_id,don_hang_id,khoa_yeu_cau)
            VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """, sanPhamId, nguoiDungId, loai, thucTe, dangGiu, thucTeSau, dangGiuSau, lyDo.strip(),
            phieuNhapId, donHangId, khoa);
    }
}
