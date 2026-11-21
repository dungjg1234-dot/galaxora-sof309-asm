package com.galaxora.donhang;

import static com.galaxora.banhang.LoiBanHang.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DichVuVoucher {
    private final JdbcTemplate jdbc;
    DichVuVoucher(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    record Giam(Long voucherId, String ma, BigDecimal soTien) {}
    record Voucher(long id, String ma, String loai, BigDecimal giaTri, BigDecimal giamToiDa, BigDecimal donToiThieu,
                   LocalDateTime batDau, LocalDateTime ketThuc, int luotToiDa, int luotDaDung, boolean kichHoat,
                   Long chuSoHuuId) {}
    record LanDung(long donHangId, String ma, BigDecimal giamGia, LocalDateTime thoiDiem) {}

    Giam tinh(String ma, long nguoiDungId, BigDecimal tamTinh, boolean khoa) {
        if (ma == null || ma.isBlank()) return new Giam(null, null, BigDecimal.ZERO);
        var voucher = jdbc.query("""
            SELECT id,ma,loai,gia_tri,giam_toi_da,don_toi_thieu,bat_dau,ket_thuc,luot_toi_da,luot_da_dung,
                kich_hoat,chu_so_huu_id FROM voucher WHERE ma=? AND NOT da_xoa
            """ + (khoa ? " FOR UPDATE" : ""), DichVuVoucher::docVoucher, ma.strip().toUpperCase(Locale.ROOT))
            .stream().findFirst().orElseThrow(DichVuVoucher::loiVoucher);
        var luc = LocalDateTime.now(ZoneOffset.UTC);
        if (!voucher.kichHoat() || luc.isBefore(voucher.batDau()) || !luc.isBefore(voucher.ketThuc())
            || voucher.luotDaDung() >= voucher.luotToiDa()
            || (voucher.chuSoHuuId() != null && voucher.chuSoHuuId() != nguoiDungId)
            || (voucher.donToiThieu() != null && tamTinh.compareTo(voucher.donToiThieu()) < 0)) {
            throw loiVoucher();
        }
        BigDecimal giam = voucher.loai().equals("PHAN_TRAM")
            ? tamTinh.multiply(voucher.giaTri()).movePointLeft(2).setScale(2, RoundingMode.HALF_UP)
            : voucher.giaTri();
        if (voucher.giamToiDa() != null) giam = giam.min(voucher.giamToiDa());
        return new Giam(voucher.id(), voucher.ma(), giam.min(tamTinh));
    }

    private static RuntimeException loiVoucher() {
        return xungDot("Voucher không tồn tại, đã hết hạn hoặc không đủ điều kiện áp dụng.");
    }

    void ghiNhan(Giam giam, long donHangId) {
        if (giam.voucherId() == null) return;
        if (jdbc.update("UPDATE voucher SET luot_da_dung=luot_da_dung+1 WHERE id=? AND luot_da_dung<luot_toi_da",
            giam.voucherId()) != 1) throw loiVoucher();
        jdbc.update("INSERT INTO lich_su_su_dung_voucher (don_hang_id,voucher_id,ma,giam_gia) VALUES (?,?,?,?)",
            donHangId, giam.voucherId(), giam.ma(), giam.soTien());
    }

    List<Voucher> danhSach() {
        return jdbc.query("""
            SELECT id,ma,loai,gia_tri,giam_toi_da,don_toi_thieu,bat_dau,ket_thuc,luot_toi_da,luot_da_dung,
                kich_hoat,chu_so_huu_id FROM voucher WHERE NOT da_xoa ORDER BY id DESC
            """, DichVuVoucher::docVoucher);
    }

    @Transactional
    void luu(Long id, DonHangController.GhiVoucher d) {
        if (!d.ketThuc().isAfter(d.batDau())
            || (d.loai().equals("PHAN_TRAM") && d.giaTri().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw khongHopLe("Kiểm tra thời hạn và tỷ lệ giảm của voucher.");
        }
        String ma = d.ma().strip().toUpperCase(Locale.ROOT);
        if (id == null) {
            jdbc.update("""
                INSERT INTO voucher (ma,loai,gia_tri,giam_toi_da,don_toi_thieu,bat_dau,ket_thuc,luot_toi_da,kich_hoat,chu_so_huu_id)
                VALUES (?,?,?,?,?,?,?,?,?,?)
                """, ma, d.loai(), d.giaTri(), d.giamToiDa(), d.donToiThieu(), d.batDau(), d.ketThuc(),
                d.luotToiDa(), d.kichHoat(), d.chuSoHuuId());
        } else if (jdbc.update("""
            UPDATE voucher SET ma=?,loai=?,gia_tri=?,giam_toi_da=?,don_toi_thieu=?,bat_dau=?,ket_thuc=?,
                luot_toi_da=?,kich_hoat=?,chu_so_huu_id=? WHERE id=? AND NOT da_xoa
            """, ma, d.loai(), d.giaTri(), d.giamToiDa(), d.donToiThieu(), d.batDau(), d.ketThuc(),
            d.luotToiDa(), d.kichHoat(), d.chuSoHuuId(), id) == 0) {
            throw khongTimThay();
        }
    }

    void xoa(long id) {
        if (jdbc.update("UPDATE voucher SET da_xoa=TRUE,kich_hoat=FALSE WHERE id=? AND NOT da_xoa", id) == 0) {
            throw khongTimThay();
        }
    }

    List<LanDung> lichSu(long id) {
        return jdbc.query("""
            SELECT don_hang_id,ma,giam_gia,created_at FROM lich_su_su_dung_voucher WHERE voucher_id=?
            ORDER BY don_hang_id DESC
            """, (r, i) -> new LanDung(r.getLong(1), r.getString(2), r.getBigDecimal(3),
                r.getObject(4, LocalDateTime.class)), id);
    }

    private static Voucher docVoucher(java.sql.ResultSet r, int i) throws java.sql.SQLException {
        return new Voucher(r.getLong(1), r.getString(2), r.getString(3), r.getBigDecimal(4), r.getBigDecimal(5),
            r.getBigDecimal(6), r.getObject(7, LocalDateTime.class), r.getObject(8, LocalDateTime.class), r.getInt(9),
            r.getInt(10), r.getBoolean(11), r.getObject(12, Long.class));
    }
}
