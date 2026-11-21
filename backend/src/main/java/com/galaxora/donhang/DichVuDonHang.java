package com.galaxora.donhang;

import static com.galaxora.banhang.LoiBanHang.*;
import static com.galaxora.donhang.DichVuDonHang.TrangThai.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.galaxora.kho.DichVuKho;
import com.galaxora.quantri.NhatKyQuanTri;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DichVuDonHang {
    enum TrangThai {
        CHO_XAC_NHAN, DA_XAC_NHAN, DANG_CHUAN_BI, DANG_GIAO, DA_GIAO_THANH_CONG, DA_HUY, GIAO_THAT_BAI
    }
    private static final String COT_DON = """
        SELECT id,ma,nguoi_dung_id,nguoi_nhan,so_dien_thoai,dia_chi,ghi_chu,tam_tinh,giam_gia,
            phi_van_chuyen,tong_tien,phuong_thuc,trang_thai,da_thu_tien,created_at FROM don_hang
        """;
    private static final RowMapper<Don> DOC_DON = (r, i) -> new Don(r.getLong(1), r.getString(2), r.getLong(3),
        r.getString(4), r.getString(5), r.getString(6), r.getString(7), r.getBigDecimal(8), r.getBigDecimal(9),
        r.getBigDecimal(10), r.getBigDecimal(11), r.getString(12), TrangThai.valueOf(r.getString(13)), r.getBoolean(14),
        r.getObject(15, LocalDateTime.class));
    private final JdbcTemplate jdbc;
    private final DichVuGioHang gioHang;
    private final DichVuVoucher voucher;
    private final DichVuKho kho;
    private final NhatKyQuanTri nhatKy;
    private final SimpleJdbcInsert themDon;

    DichVuDonHang(JdbcTemplate jdbc, DichVuGioHang gioHang, DichVuVoucher voucher, DichVuKho kho, NhatKyQuanTri nhatKy) {
        this.jdbc = jdbc;
        this.gioHang = gioHang;
        this.voucher = voucher;
        this.kho = kho;
        this.nhatKy = nhatKy;
        themDon = new SimpleJdbcInsert(jdbc).withTableName("don_hang").usingGeneratedKeyColumns("id")
            .usingColumns("ma", "nguoi_dung_id", "khoa_yeu_cau", "nguoi_nhan", "so_dien_thoai", "dia_chi", "ghi_chu",
                "tam_tinh", "giam_gia", "phi_van_chuyen", "tong_tien", "trang_thai");
    }

    record Don(long id, String ma, long nguoiDungId, String nguoiNhan, String soDienThoai, String diaChi, String ghiChu,
               BigDecimal tamTinh, BigDecimal giamGia, BigDecimal phiVanChuyen, BigDecimal tongTien, String phuongThuc,
               TrangThai trangThai, boolean daThuTien, LocalDateTime thoiDiem) {}
    record Dong(long sanPhamId, String sku, String ten, BigDecimal donGia, int soLuong) {}
    record LichSu(TrangThai trangThai, String lyDo, LocalDateTime thoiDiem) {}
    record YeuCauHuy(long id, String trangThai, String lyDo, String lyDoXuLy, LocalDateTime xuLyLuc,
                     LocalDateTime thoiDiem) {}
    record ChiTiet(Don don, List<Dong> chiTiet, List<LichSu> lichSu, List<YeuCauHuy> yeuCauHuy) {}
    record TongTien(BigDecimal tamTinh, BigDecimal giamGia, BigDecimal phiVanChuyen, BigDecimal tongTien) {}

    BigDecimal phiVanChuyen() {
        return jdbc.queryForObject("SELECT phi_van_chuyen FROM cau_hinh_ban_hang WHERE id=1", BigDecimal.class);
    }

    void datPhi(BigDecimal phi) {
        jdbc.update("UPDATE cau_hinh_ban_hang SET phi_van_chuyen=? WHERE id=1", phi);
    }

    List<Don> danhSach(Long nguoiDungId, int trang, int kichThuoc) {
        return jdbc.query(COT_DON + " WHERE (? IS NULL OR nguoi_dung_id=?) ORDER BY id DESC LIMIT ? OFFSET ?",
            DOC_DON, nguoiDungId, nguoiDungId, kichThuoc, (long) trang * kichThuoc);
    }

    @Transactional(readOnly = true)
    ChiTiet chiTiet(long id, Long nguoiDungId) {
        var don = layDon(id, nguoiDungId, false);
        var lichSu = jdbc.query("""
            SELECT trang_thai,ly_do,created_at FROM lich_su_trang_thai_don WHERE don_hang_id=? ORDER BY id
            """, (r, i) -> new LichSu(TrangThai.valueOf(r.getString(1)), r.getString(2),
                r.getObject(3, LocalDateTime.class)), id);
        var yeuCau = jdbc.query("""
            SELECT id,trang_thai,ly_do,ly_do_xu_ly,xu_ly_luc,created_at FROM yeu_cau_huy_don WHERE don_hang_id=? ORDER BY id
            """, (r, i) -> new YeuCauHuy(r.getLong(1), r.getString(2), r.getString(3), r.getString(4),
                r.getObject(5, LocalDateTime.class), r.getObject(6, LocalDateTime.class)), id);
        return new ChiTiet(don, cacDong(id), lichSu, yeuCau);
    }

    @Transactional(readOnly = true)
    TongTien baoGia(long id, String maVoucher) {
        var dong = kiemTraGio(id, false);
        var tamTinh = DichVuGioHang.tinhTamTinh(dong);
        return tinhTien(tamTinh, voucher.tinh(maVoucher, id, tamTinh, false));
    }

    @Transactional
    long dat(long nguoiDungId, DonHangController.DatDon d) {
        gioHang.khoaNguoiMua(nguoiDungId);
        var cu = jdbc.queryForList("SELECT id FROM don_hang WHERE nguoi_dung_id=? AND khoa_yeu_cau=?",
            Long.class, nguoiDungId, d.khoaYeuCau());
        if (!cu.isEmpty()) return cu.getFirst();
        var dong = kiemTraGio(nguoiDungId, true);
        var tamTinh = DichVuGioHang.tinhTamTinh(dong);
        var giam = voucher.tinh(d.maVoucher(), nguoiDungId, tamTinh, true);
        var tien = tinhTien(tamTinh, giam);
        if (tien.tongTien().compareTo(d.tongTienDaXacNhan()) != 0) {
            throw xungDot("Tổng tiền đã thay đổi. Hãy tính lại trước khi đặt hàng.");
        }
        var thamSo = new HashMap<String, Object>();
        thamSo.put("ma", "DH-" + UUID.randomUUID().toString().toUpperCase(java.util.Locale.ROOT));
        thamSo.put("nguoi_dung_id", nguoiDungId);
        thamSo.put("khoa_yeu_cau", d.khoaYeuCau());
        thamSo.put("nguoi_nhan", d.nguoiNhan().strip());
        thamSo.put("so_dien_thoai", d.soDienThoai());
        thamSo.put("dia_chi", d.diaChi().strip());
        thamSo.put("ghi_chu", d.ghiChu() == null ? "" : d.ghiChu().strip());
        thamSo.put("tam_tinh", tien.tamTinh());
        thamSo.put("giam_gia", tien.giamGia());
        thamSo.put("phi_van_chuyen", tien.phiVanChuyen());
        thamSo.put("tong_tien", tien.tongTien());
        thamSo.put("trang_thai", CHO_XAC_NHAN.name());
        long id = themDon.executeAndReturnKey(thamSo).longValue();
        for (var muc : dong) {
            jdbc.update("""
                INSERT INTO chi_tiet_don_hang (don_hang_id,san_pham_id,sku,ten_san_pham,don_gia,so_luong)
                VALUES (?,?,?,?,?,?)
                """, id, muc.sanPhamId(), muc.sku(), muc.ten(), muc.gia(), muc.soLuong());
        }
        voucher.ghiNhan(giam, id);
        lichSu(id, nguoiDungId, CHO_XAC_NHAN, "Tạo đơn COD");
        gioHang.xoaSauKhiDat(nguoiDungId);
        return id;
    }

    @Transactional
    void chuyen(long id, long admin, DonHangController.ChuyenTrangThai d) {
        var don = layDon(id, null, true);
        var moi = d.trangThai();
        if (don.trangThai() == moi) return;
        boolean hopLe = switch (don.trangThai()) {
            case CHO_XAC_NHAN -> moi == DA_XAC_NHAN || moi == DA_HUY;
            case DA_XAC_NHAN -> moi == DANG_CHUAN_BI;
            case DANG_CHUAN_BI -> moi == DANG_GIAO;
            case DANG_GIAO -> moi == DA_GIAO_THANH_CONG || moi == GIAO_THAT_BAI;
            default -> false;
        };
        if (!hopLe) throw xungDot("Chuyển trạng thái đơn hàng không hợp lệ.");
        if (moi == DANG_GIAO && dangChoDuyet(id)) throw xungDot("Hãy xử lý yêu cầu hủy trước khi giao hàng.");
        if ((moi == DA_HUY || moi == GIAO_THAT_BAI) && (d.lyDo() == null || d.lyDo().isBlank())) {
            throw khongHopLe("Thao tác này cần lý do.");
        }
        if (moi == DA_GIAO_THANH_CONG && !d.daThuTien()) {
            throw khongHopLe("Xác nhận đã thu tiền COD khi giao thành công.");
        }
        if (moi == DA_XAC_NHAN) {
            for (var dong : cacDong(id)) kho.giu(dong.sanPhamId(), admin, id, dong.soLuong());
        } else if (moi == DA_GIAO_THANH_CONG || moi == GIAO_THAT_BAI) {
            ketThucGiu(id, admin, moi == DA_GIAO_THANH_CONG);
        }
        capNhat(don, admin, moi, d.lyDo());
        nhatKy.ghiTrongGiaoDich(admin, "DON_HANG " + don.trangThai().name() + " -> " + moi.name(),
            "/api/quan-tri/don-hang/" + id + "/trang-thai");
    }

    @Transactional
    void huyTruocXacNhan(long id, long nguoiDungId) {
        var don = layDon(id, nguoiDungId, true);
        if (don.trangThai() == DA_HUY) return;
        if (don.trangThai() != CHO_XAC_NHAN) throw xungDot("Đơn đã được xác nhận hoặc đã kết thúc.");
        capNhat(don, nguoiDungId, DA_HUY, "Khách tự hủy trước xác nhận");
    }

    @Transactional
    void guiYeuCauHuy(long id, long nguoiDungId, String lyDo) {
        var don = layDon(id, nguoiDungId, true);
        kiemTraChoPhepYeuCau(don);
        if (dangChoDuyet(id)) throw xungDot("Đơn đã có yêu cầu hủy đang chờ duyệt.");
        jdbc.update("""
            INSERT INTO yeu_cau_huy_don (don_hang_id,nguoi_dung_id,trang_thai,ly_do) VALUES (?,?,'CHO_DUYET',?)
            """, id, nguoiDungId, lyDo.strip());
    }

    @Transactional
    void xuLyHuy(long id, long yeuCauId, long admin, DonHangController.XuLyHuy d) {
        var don = layDon(id, null, true);
        var trangThai = jdbc.queryForList("SELECT trang_thai FROM yeu_cau_huy_don WHERE id=? AND don_hang_id=? FOR UPDATE",
            String.class, yeuCauId, id).stream().findFirst().orElseThrow(() -> khongTimThay());
        String moi = d.duyet() ? "DA_DUYET" : "TU_CHOI";
        if (trangThai.equals(moi)) return;
        if (!trangThai.equals("CHO_DUYET")) throw xungDot("Yêu cầu hủy đã được xử lý.");
        kiemTraChoPhepYeuCau(don);
        jdbc.update("""
            UPDATE yeu_cau_huy_don SET trang_thai=?,nguoi_xu_ly_id=?,ly_do_xu_ly=?,xu_ly_luc=UTC_TIMESTAMP(6)
            WHERE id=?
            """, moi, admin, d.lyDo().strip(), yeuCauId);
        if (d.duyet()) {
            ketThucGiu(id, admin, false);
            capNhat(don, admin, DA_HUY, d.lyDo());
        } else {
            lichSu(id, admin, don.trangThai(), "Từ chối yêu cầu hủy #" + yeuCauId);
        }
        nhatKy.ghiTrongGiaoDich(admin, "YEU_CAU_HUY CHO_DUYET -> " + moi,
            "/api/quan-tri/don-hang/" + id + "/yeu-cau-huy/" + yeuCauId);
    }

    private void ketThucGiu(long id, long admin, boolean daBan) {
        for (var dong : cacDong(id)) kho.ketThucGiu(dong.sanPhamId(), admin, id, dong.soLuong(), daBan);
    }

    private boolean dangChoDuyet(long id) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
            "SELECT EXISTS (SELECT 1 FROM yeu_cau_huy_don WHERE dang_cho=?)", Boolean.class, id));
    }

    private static void kiemTraChoPhepYeuCau(Don don) {
        if (don.trangThai() != DA_XAC_NHAN && don.trangThai() != DANG_CHUAN_BI) {
            throw xungDot("Chỉ xử lý yêu cầu hủy từ Đã xác nhận đến hết Đang chuẩn bị.");
        }
    }

    private void capNhat(Don don, long nguoiDungId, TrangThai moi, String lyDo) {
        jdbc.update("UPDATE don_hang SET trang_thai=?,da_thu_tien=? WHERE id=?",
            moi.name(), moi == DA_GIAO_THANH_CONG, don.id());
        lichSu(don.id(), nguoiDungId, moi, lyDo == null ? "" : lyDo.strip());
    }

    private void lichSu(long id, long nguoiDungId, TrangThai trangThai, String lyDo) {
        jdbc.update("INSERT INTO lich_su_trang_thai_don (don_hang_id,nguoi_dung_id,trang_thai,ly_do) VALUES (?,?,?,?)",
            id, nguoiDungId, trangThai.name(), lyDo);
    }

    private List<Dong> cacDong(long id) {
        return jdbc.query("""
            SELECT san_pham_id,sku,ten_san_pham,don_gia,so_luong
            FROM chi_tiet_don_hang WHERE don_hang_id=? ORDER BY san_pham_id
            """, (r, i) -> new Dong(r.getLong(1), r.getString(2), r.getString(3), r.getBigDecimal(4), r.getInt(5)), id);
    }

    private Don layDon(long id, Long nguoiDungId, boolean khoa) {
        return jdbc.query(COT_DON + " WHERE id=? AND (? IS NULL OR nguoi_dung_id=?)" + (khoa ? " FOR UPDATE" : ""),
            DOC_DON, id, nguoiDungId, nguoiDungId).stream().findFirst().orElseThrow(() -> khongTimThay());
    }

    private List<DichVuGioHang.Dong> kiemTraGio(long id, boolean khoa) {
        var dong = gioHang.docDong(id, khoa);
        if (dong.isEmpty()) throw xungDot("Giỏ hàng trống.");
        if (dong.stream().anyMatch(d -> !d.dangBan() || d.soLuong() > d.khaDung())) {
            throw xungDot("Giỏ có sản phẩm đã ngừng bán hoặc không đủ tồn kho.");
        }
        return dong;
    }

    private TongTien tinhTien(BigDecimal tamTinh, DichVuVoucher.Giam giam) {
        var phi = phiVanChuyen();
        if (phi == null) throw xungDot("Cửa hàng chưa cấu hình phí giao hàng COD.");
        var tong = tamTinh.subtract(giam.soTien()).add(phi);
        var toiDa = new BigDecimal("9999999999999.99");
        if (tong.compareTo(toiDa) > 0 || tamTinh.compareTo(toiDa) > 0) throw xungDot("Giá trị đơn vượt giới hạn.");
        return new TongTien(tamTinh, giam.soTien(), phi, tong);
    }
}
