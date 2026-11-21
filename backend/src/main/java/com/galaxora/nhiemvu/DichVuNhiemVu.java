package com.galaxora.nhiemvu;

import static com.galaxora.banhang.LoiBanHang.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DichVuNhiemVu {
    private static final String HANH_TRINH = "HE_MAT_TROI";
    private static final String VOUCHER = "VOUCHER_HANH_TRINH";
    static final List<List<String>> CAC_CHUONG = List.of(
        List.of("mercury", "venus", "earth", "mars"),
        List.of("jupiter", "saturn", "uranus", "neptune"),
        List.of("moon", "pluto"));
    private final JdbcTemplate jdbc;
    private final DichVuHoSoKhamPha hoSo;
    private final QuanLyNhiemVu quanLy;

    DichVuNhiemVu(JdbcTemplate jdbc, DichVuHoSoKhamPha hoSo, QuanLyNhiemVu quanLy) {
        this.jdbc = jdbc;
        this.hoSo = hoSo;
        this.quanLy = quanLy;
    }

    record Chuong(int so, List<String> thienThe, boolean mo, boolean hoanThanh) {}
    record TienDo(DichVuHoSoKhamPha.HoSo hoSo, List<Chuong> cacChuong, boolean voucherDangCho,
                  List<QuanLyNhiemVu.TienDoNhiemVu> nhiemVu) {}
    record MauVoucher(
        @NotBlank @Pattern(regexp = "PHAN_TRAM|SO_TIEN") String loai,
        @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal giaTri,
        @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giamToiDa,
        @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal donToiThieu,
        @NotNull LocalDateTime batDau, @NotNull LocalDateTime ketThuc,
        @Positive int luotToiDa, boolean kichHoat) {}

    @Transactional(readOnly = true)
    public TienDo xem(long id) {
        return taoTienDo(id);
    }

    @Transactional
    public TienDo khamPha(long id, String ma) {
        if (CAC_CHUONG.stream().noneMatch(c -> c.contains(ma))) throw khongTimThay();
        khoaTaiKhoan(id);
        ghiXp(id, "KHAM_PHA", ma, 10);
        if (daKhamPha(id).size() == 10) {
            huyHieu(id, "KHAM_PHA");
            ghiXp(id, "HANH_TRINH", HANH_TRINH, 100);
            huyHieu(id, "HANH_TRINH");
            capVoucher(id, VOUCHER, null);
        }
        return doiChieuDaKhoa(id);
    }

    @Transactional
    public TienDo vatTheAn(long id) {
        khoaTaiKhoan(id);
        ghiXp(id, "VAT_THE_AN", HANH_TRINH, 50);
        huyHieu(id, "VAT_THE_AN");
        return doiChieuDaKhoa(id);
    }

    @Transactional
    public TienDo nhanVoucher(long id) {
        khoaTaiKhoan(id);
        if (daKhamPha(id).size() != 10) throw xungDot("Hãy hoàn thành hành trình Hệ Mặt Trời trước.");
        capVoucher(id, VOUCHER, null);
        return taoTienDo(id);
    }

    @Transactional
    public TienDo doiChieu(long id) {
        khoaTaiKhoan(id);
        return doiChieuDaKhoa(id);
    }

    private TienDo doiChieuDaKhoa(long id) {
        var daCo = daKhamPha(id);
        boolean daTim = coPhanThuong(id, "VAT_THE_AN");
        var cacChuong = taoChuong(daCo);
        for (var n : quanLy.dangHoatDong()) {
            boolean dat = switch (n.duLieu().loaiDieuKien()) {
                case THIEN_THE -> daCo.contains(n.duLieu().maDieuKien());
                case CHUONG -> cacChuong.get(Integer.parseInt(n.duLieu().maDieuKien()) - 1).hoanThanh();
                case DAU_HIEU_AN -> daTim;
            };
            if (!dat || !quanLy.ghiHoanThanh(id, n.id())) continue;
            if (n.duLieu().xp() > 0) ghiXp(id, "NHIEM_VU", "NV_" + n.id(), n.duLieu().xp());
            if (n.duLieu().tenHuyHieu() != null) huyHieu(id, "NV_" + n.id());
        }
        for (long nhiemVuId : quanLy.voucherDangCho(id)) {
            capVoucher(id, "VOUCHER_NV_" + nhiemVuId, nhiemVuId);
        }
        return taoTienDo(id);
    }

    private void khoaTaiKhoan(long id) {
        jdbc.queryForObject("SELECT id FROM nguoi_dung WHERE id=? FOR UPDATE", Long.class, id);
    }

    private Set<String> daKhamPha(long id) {
        return Set.copyOf(jdbc.queryForList("""
            SELECT ma_noi_dung FROM lich_su_xp WHERE nguoi_dung_id=? AND nguon='KHAM_PHA'
            """, String.class, id));
    }

    private void ghiXp(long id, String nguon, String ma, int xp) {
        jdbc.update("""
            INSERT INTO lich_su_xp(nguoi_dung_id,nguon,ma_noi_dung,so_xp) VALUES (?,?,?,?)
            ON DUPLICATE KEY UPDATE id=id
            """, id, nguon, ma, xp);
    }

    private void huyHieu(long id, String ma) {
        jdbc.update("""
            INSERT INTO lich_su_phan_thuong(nguoi_dung_id,ma,loai) VALUES (?,?,'HUY_HIEU')
            ON DUPLICATE KEY UPDATE ma=lich_su_phan_thuong.ma
            """, id, ma);
    }

    private boolean coPhanThuong(long id, String ma) {
        return Boolean.TRUE.equals(jdbc.queryForObject("""
            SELECT EXISTS(SELECT 1 FROM lich_su_phan_thuong WHERE nguoi_dung_id=? AND ma=?)
            """, Boolean.class, id, ma));
    }

    private void capVoucher(long id, String maPhanThuong, Long nhiemVuId) {
        if (coPhanThuong(id, maPhanThuong)) return;
        String bang = nhiemVuId == null ? "cau_hinh_voucher_hanh_trinh" : "cau_hinh_voucher_nhiem_vu";
        long cauHinhId = nhiemVuId == null ? 1 : nhiemVuId;
        if (jdbc.update(("""
            UPDATE %s SET luot_da_cap=luot_da_cap+1
            WHERE id=? AND kich_hoat AND luot_da_cap<luot_toi_da
                AND bat_dau<=UTC_TIMESTAMP(6) AND ket_thuc>UTC_TIMESTAMP(6)
            """).formatted(bang), cauHinhId) == 0) return;
        String ma = (nhiemVuId == null ? "HT-" : "NV-")
            + UUID.randomUUID().toString().replace("-", "").toUpperCase(java.util.Locale.ROOT);
        jdbc.update(("""
            INSERT INTO voucher(ma,loai,gia_tri,giam_toi_da,don_toi_thieu,bat_dau,ket_thuc,luot_toi_da,kich_hoat,chu_so_huu_id)
            SELECT ?,loai,gia_tri,giam_toi_da,don_toi_thieu,bat_dau,ket_thuc,1,TRUE,?
            FROM %s WHERE id=?
            """).formatted(bang), ma, id, cauHinhId);
        jdbc.update("""
            INSERT INTO lich_su_phan_thuong(nguoi_dung_id,ma,loai,voucher_id)
            SELECT ?,?,'VOUCHER',id FROM voucher WHERE ma=?
            """, id, maPhanThuong, ma);
    }

    private TienDo taoTienDo(long id) {
        var h = hoSo.xem(id);
        var daCo = h.lichSuXp().stream().filter(x -> x.nguon().equals("KHAM_PHA"))
            .map(DichVuHoSoKhamPha.GhiNhanXp::maNoiDung).collect(java.util.stream.Collectors.toSet());
        var cacChuong = taoChuong(daCo);
        boolean voucherDangCho = cacChuong.get(cacChuong.size() - 1).hoanThanh()
            && h.phanThuong().stream().noneMatch(p -> p.ma().equals(VOUCHER));
        return new TienDo(h, cacChuong, voucherDangCho, quanLy.tienDo(id));
    }

    private List<Chuong> taoChuong(Set<String> daCo) {
        boolean mo = true;
        var cacChuong = new ArrayList<Chuong>();
        for (var ds : CAC_CHUONG) {
            boolean xong = mo && daCo.containsAll(ds);
            cacChuong.add(new Chuong(cacChuong.size() + 1, ds, mo, xong));
            mo = xong;
        }
        return List.copyOf(cacChuong);
    }

    record CauHinh(MauVoucher mau, int luotDaCap) {}

    public List<CauHinh> cauHinh() {
        return jdbc.query("SELECT * FROM cau_hinh_voucher_hanh_trinh WHERE id=1 AND NOT da_xoa", (r,i) ->
            new CauHinh(new MauVoucher(r.getString("loai"), r.getBigDecimal("gia_tri"), r.getBigDecimal("giam_toi_da"),
                r.getBigDecimal("don_toi_thieu"), r.getObject("bat_dau", LocalDateTime.class),
                r.getObject("ket_thuc", LocalDateTime.class), r.getInt("luot_toi_da"), r.getBoolean("kich_hoat")),
                r.getInt("luot_da_cap")));
    }

    @Transactional
    public void luuVoucher(MauVoucher m) {
        kiemTraVoucher(m);
        luuCauHinhVoucher(jdbc, "cau_hinh_voucher_hanh_trinh", 1, m);
    }

    static void kiemTraVoucher(MauVoucher m) {
        if (!m.ketThuc().isAfter(m.batDau()) || (m.loai().equals("PHAN_TRAM") && m.giaTri().compareTo(BigDecimal.valueOf(100)) > 0))
            throw khongHopLe("Kiểm tra thời hạn và giá trị voucher.");
    }

    void xoaMauVoucher() {
        xoaCauHinhVoucher(jdbc, "cau_hinh_voucher_hanh_trinh", 1);
    }

    static void xoaCauHinhVoucher(JdbcTemplate jdbc, String bang, long id) {
        if (jdbc.update("UPDATE " + bang + " SET kich_hoat=FALSE,da_xoa=TRUE WHERE id=? AND NOT da_xoa", id) == 0) {
            throw com.galaxora.banhang.LoiBanHang.khongTimThay();
        }
    }

    static void luuCauHinhVoucher(JdbcTemplate jdbc, String bang, long id, MauVoucher m) {
        jdbc.update(("""
            INSERT INTO %s(id,loai,gia_tri,giam_toi_da,don_toi_thieu,bat_dau,ket_thuc,luot_toi_da,kich_hoat)
            VALUES (?,?,?,?,?,?,?,?,?) AS moi
            ON DUPLICATE KEY UPDATE loai=moi.loai,gia_tri=moi.gia_tri,giam_toi_da=moi.giam_toi_da,
              don_toi_thieu=moi.don_toi_thieu,bat_dau=moi.bat_dau,ket_thuc=moi.ket_thuc,
              luot_toi_da=moi.luot_toi_da,kich_hoat=moi.kich_hoat,da_xoa=FALSE
            """).formatted(bang), id, m.loai(), m.giaTri(), m.giamToiDa(), m.donToiThieu(), m.batDau(), m.ketThuc(), m.luotToiDa(), m.kichHoat());
    }
}
