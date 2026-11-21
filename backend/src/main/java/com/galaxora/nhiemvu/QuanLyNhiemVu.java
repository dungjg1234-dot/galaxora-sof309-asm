package com.galaxora.nhiemvu;

import static com.galaxora.banhang.LoiBanHang.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class QuanLyNhiemVu {
    enum LoaiDieuKien { THIEN_THE, CHUONG, DAU_HIEU_AN }

    record Tao(@NotBlank @Size(max=160) String ten, @NotBlank @Size(max=1000) String moTa,
        @NotNull LoaiDieuKien loaiDieuKien, @Size(max=48) String maDieuKien, @PositiveOrZero int xp,
        @Size(max=160) String tenHuyHieu, boolean coVoucher, boolean kichHoat) {}
    record Sua(@NotBlank @Size(max=160) String ten, @NotBlank @Size(max=1000) String moTa,
        @NotNull Boolean kichHoat) {}
    record NhiemVu(long id, Tao duLieu, DichVuNhiemVu.CauHinh voucher) {}
    record TienDoNhiemVu(long id, String ten, String moTa, LoaiDieuKien loaiDieuKien, String maDieuKien,
        int xp, String tenHuyHieu, boolean coVoucher, boolean hoanThanh, boolean voucherDangCho) {}

    private final JdbcTemplate jdbc;

    QuanLyNhiemVu(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    List<NhiemVu> danhSach() {
        return jdbc.query("""
            SELECT n.*, v.loai, v.gia_tri, v.giam_toi_da, v.don_toi_thieu, v.bat_dau, v.ket_thuc,
                v.luot_toi_da, v.luot_da_cap, v.kich_hoat AS voucher_kich_hoat
            FROM nhiem_vu n LEFT JOIN cau_hinh_voucher_nhiem_vu v ON v.id=n.id AND NOT v.da_xoa
            WHERE NOT n.da_xoa ORDER BY n.id
            """, (r, i) -> new NhiemVu(r.getLong("id"), doc(r), r.getString("loai") == null ? null :
                new DichVuNhiemVu.CauHinh(new DichVuNhiemVu.MauVoucher(r.getString("loai"), r.getBigDecimal("gia_tri"),
                    r.getBigDecimal("giam_toi_da"), r.getBigDecimal("don_toi_thieu"),
                    r.getObject("bat_dau", LocalDateTime.class), r.getObject("ket_thuc", LocalDateTime.class),
                    r.getInt("luot_toi_da"), r.getBoolean("voucher_kich_hoat")), r.getInt("luot_da_cap"))));
    }

    List<NhiemVu> dangHoatDong() {
        return jdbc.query("SELECT * FROM nhiem_vu WHERE kich_hoat ORDER BY id",
            (r, i) -> new NhiemVu(r.getLong("id"), doc(r), null));
    }

    private static Tao doc(ResultSet r) throws SQLException {
        return new Tao(r.getString("ten"), r.getString("mo_ta"), LoaiDieuKien.valueOf(r.getString("loai_dieu_kien")),
            r.getString("ma_dieu_kien"), r.getInt("xp"), r.getString("ten_huy_hieu"),
            r.getBoolean("co_voucher"), r.getBoolean("kich_hoat"));
    }

    long tao(Tao d) {
        String ma = d.maDieuKien();
        boolean hopLe = switch (d.loaiDieuKien()) {
            case THIEN_THE -> ma != null && DichVuNhiemVu.CAC_CHUONG.stream().anyMatch(c -> c.contains(ma));
            case CHUONG -> ma != null && ma.matches("[1-3]");
            case DAU_HIEU_AN -> ma == null;
        };
        if (!hopLe) throw khongHopLe("Điều kiện nhiệm vụ không hợp lệ.");
        String tenHuyHieu = d.tenHuyHieu() == null ? null : d.tenHuyHieu().strip();
        if (tenHuyHieu != null && tenHuyHieu.isEmpty()) tenHuyHieu = null;
        if (d.xp() == 0 && tenHuyHieu == null && !d.coVoucher()) throw khongHopLe("Hãy chọn ít nhất một phần thưởng.");
        var khoa = new GeneratedKeyHolder();
        String huyHieu = tenHuyHieu;
        jdbc.update(c -> {
            var ps = c.prepareStatement("""
                INSERT INTO nhiem_vu(ten,mo_ta,loai_dieu_kien,ma_dieu_kien,xp,ten_huy_hieu,co_voucher,kich_hoat)
                VALUES (?,?,?,?,?,?,?,?)
                """, java.sql.Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, d.ten().strip());
            ps.setString(2, d.moTa().strip());
            ps.setString(3, d.loaiDieuKien().name());
            ps.setString(4, ma);
            ps.setInt(5, d.xp());
            ps.setString(6, huyHieu);
            ps.setBoolean(7, d.coVoucher());
            ps.setBoolean(8, d.kichHoat());
            return ps;
        }, khoa);
        return java.util.Objects.requireNonNull(khoa.getKey()).longValue();
    }

    void sua(long id, Sua d) {
        if (jdbc.update("UPDATE nhiem_vu SET ten=?,mo_ta=?,kich_hoat=? WHERE id=? AND NOT da_xoa",
            d.ten().strip(), d.moTa().strip(), d.kichHoat(), id) == 0) throw khongTimThay();
    }

    void xoa(long id) {
        if (jdbc.update("UPDATE nhiem_vu SET da_xoa=TRUE,kich_hoat=FALSE WHERE id=? AND NOT da_xoa", id) == 0) {
            throw khongTimThay();
        }
    }

    @Transactional
    public void luuVoucher(long id, DichVuNhiemVu.MauVoucher m) {
        var choPhep = jdbc.queryForList("SELECT co_voucher FROM nhiem_vu WHERE id=? AND NOT da_xoa FOR UPDATE", Boolean.class, id);
        if (choPhep.isEmpty()) throw khongTimThay();
        if (!choPhep.getFirst()) throw khongHopLe("Nhiệm vụ này không cấp voucher.");
        DichVuNhiemVu.kiemTraVoucher(m);
        DichVuNhiemVu.luuCauHinhVoucher(jdbc, "cau_hinh_voucher_nhiem_vu", id, m);
    }

    boolean ghiHoanThanh(long id, long nhiemVuId) {
        // Lời gọi giữ khóa tài khoản, nên hai yêu cầu không thể cấp cùng phần thưởng.
        if (jdbc.queryForList("SELECT id FROM nhiem_vu WHERE id=? AND kich_hoat FOR SHARE", Long.class, nhiemVuId).isEmpty()) {
            return false;
        }
        var daCo = jdbc.queryForList("""
            SELECT nhiem_vu_id FROM nhiem_vu_hoan_thanh WHERE nguoi_dung_id=? AND nhiem_vu_id=?
            """, Long.class, id, nhiemVuId);
        if (!daCo.isEmpty()) return false;
        jdbc.update("INSERT INTO nhiem_vu_hoan_thanh(nguoi_dung_id,nhiem_vu_id) VALUES (?,?)", id, nhiemVuId);
        return true;
    }

    void xoaVoucher(long id) {
        DichVuNhiemVu.xoaCauHinhVoucher(jdbc, "cau_hinh_voucher_nhiem_vu", id);
    }

    List<Long> voucherDangCho(long id) {
        return jdbc.queryForList("""
            SELECT n.id FROM nhiem_vu n JOIN nhiem_vu_hoan_thanh h ON h.nhiem_vu_id=n.id
            WHERE h.nguoi_dung_id=? AND n.co_voucher ORDER BY n.id
            """, Long.class, id);
    }

    List<TienDoNhiemVu> tienDo(long id) {
        return jdbc.query("""
            SELECT n.id,n.ten,n.mo_ta,n.loai_dieu_kien,n.ma_dieu_kien,n.xp,n.ten_huy_hieu,n.co_voucher,
                h.nhiem_vu_id IS NOT NULL AS da_xong,
                h.nhiem_vu_id IS NOT NULL AND n.co_voucher AND p.ma IS NULL AS voucher_dang_cho
            FROM nhiem_vu n
            LEFT JOIN nhiem_vu_hoan_thanh h ON h.nhiem_vu_id=n.id AND h.nguoi_dung_id=?
            LEFT JOIN lich_su_phan_thuong p ON p.nguoi_dung_id=h.nguoi_dung_id AND p.ma=CONCAT('VOUCHER_NV_',n.id)
            WHERE h.nhiem_vu_id IS NOT NULL OR (n.kich_hoat AND n.loai_dieu_kien <> 'DAU_HIEU_AN')
            ORDER BY n.id
            """, (r, i) -> new TienDoNhiemVu(r.getLong("id"), r.getString("ten"), r.getString("mo_ta"),
                LoaiDieuKien.valueOf(r.getString("loai_dieu_kien")), r.getString("ma_dieu_kien"), r.getInt("xp"),
                r.getString("ten_huy_hieu"), r.getBoolean("co_voucher"), r.getBoolean("da_xong"),
                r.getBoolean("voucher_dang_cho")), id);
    }
}
