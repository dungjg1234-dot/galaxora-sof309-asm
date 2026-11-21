package com.galaxora.donhang;

import static com.galaxora.banhang.LoiBanHang.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DichVuGioHang {
    private final JdbcTemplate jdbc;
    DichVuGioHang(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    record Dong(long sanPhamId, String sku, String ten, BigDecimal gia, int soLuong, int khaDung, boolean dangBan,
               String anh) {}
    record Gio(List<Dong> chiTiet, BigDecimal tamTinh) {}

    Gio xem(long id) {
        var dong = docDong(id, false);
        return new Gio(dong, tinhTamTinh(dong));
    }

    void khoaNguoiMua(long id) {
        jdbc.queryForObject("SELECT id FROM nguoi_dung WHERE id=? FOR UPDATE", Long.class, id);
    }

    @Transactional
    void sua(long id, long sanPhamId, int soLuong) {
        khoaNguoiMua(id);
        ghiSoLuong(id, sanPhamId, soLuong);
    }

    @Transactional
    void them(long id, long sanPhamId, int soLuong) {
        khoaNguoiMua(id);
        int daCo = jdbc.queryForList("""
            SELECT so_luong FROM chi_tiet_gio_hang WHERE nguoi_dung_id=? AND san_pham_id=?
            """, Integer.class, id, sanPhamId).stream().findFirst().orElse(0);
        long tong = (long) daCo + soLuong;
        if (tong > Integer.MAX_VALUE) throw xungDot("Số lượng vượt tồn kho khả dụng.");
        ghiSoLuong(id, sanPhamId, (int) tong);
    }

    private void ghiSoLuong(long id, long sanPhamId, int soLuong) {
        if (soLuong == 0) {
            jdbc.update("DELETE FROM chi_tiet_gio_hang WHERE nguoi_dung_id=? AND san_pham_id=?", id, sanPhamId);
            return;
        }
        var khaDung = jdbc.queryForList("""
            SELECT k.so_luong_thuc_te-k.so_luong_dang_giu
            FROM san_pham s JOIN danh_muc d ON d.id=s.danh_muc_id JOIN ton_kho k ON k.san_pham_id=s.id
            WHERE s.id=? AND s.hien_thi AND s.kinh_doanh AND d.hien_thi
            """, Integer.class, sanPhamId).stream().findFirst().orElseThrow(() -> khongTimThay());
        if (soLuong > khaDung) throw xungDot("Số lượng vượt tồn kho khả dụng.");
        jdbc.update("INSERT INTO gio_hang (nguoi_dung_id) VALUES (?) ON DUPLICATE KEY UPDATE nguoi_dung_id=nguoi_dung_id", id);
        jdbc.update("""
            INSERT INTO chi_tiet_gio_hang (nguoi_dung_id,san_pham_id,so_luong) VALUES (?,?,?) AS moi
            ON DUPLICATE KEY UPDATE so_luong=moi.so_luong
            """, id, sanPhamId, soLuong);
    }

    List<Dong> docDong(long id, boolean khoa) {
        return jdbc.query("""
            SELECT s.id,s.sku,s.ten,s.gia,g.so_luong,k.so_luong_thuc_te-k.so_luong_dang_giu,
                (s.hien_thi AND s.kinh_doanh AND d.hien_thi),
                (SELECT a.duong_dan FROM hinh_anh_san_pham a WHERE a.san_pham_id=s.id ORDER BY a.id LIMIT 1)
            FROM chi_tiet_gio_hang g JOIN san_pham s ON s.id=g.san_pham_id
            JOIN danh_muc d ON d.id=s.danh_muc_id JOIN ton_kho k ON k.san_pham_id=s.id
            WHERE g.nguoi_dung_id=? ORDER BY s.id
            """ + (khoa ? " FOR SHARE" : ""),
            (r, i) -> new Dong(r.getLong(1), r.getString(2), r.getString(3), r.getBigDecimal(4), r.getInt(5),
                r.getInt(6), r.getBoolean(7), r.getString(8)), id);
    }

    static BigDecimal tinhTamTinh(List<Dong> dong) {
        return dong.stream().map(d -> d.gia().multiply(BigDecimal.valueOf(d.soLuong())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    void xoaSauKhiDat(long id) {
        jdbc.update("DELETE FROM chi_tiet_gio_hang WHERE nguoi_dung_id=?", id);
    }
}
