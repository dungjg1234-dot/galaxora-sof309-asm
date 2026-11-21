package com.galaxora.taikhoan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
class KhoTaiKhoan {
    private final JdbcClient jdbc;

    KhoTaiKhoan(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    record NguoiDung(long id, String matKhauBam, LocalDateTime khoaLuc, LocalDateTime capNhatLuc) {
        boolean dangKhoa() {
            return khoaLuc != null;
        }
    }

    long taoNguoiDung(String hoTen, String email, String emailChuanHoa, String dienThoai,
                      String matKhauBam, LocalDateTime luc) {
        var khoa = new GeneratedKeyHolder();
        jdbc.sql("""
            INSERT INTO nguoi_dung
                (ho_ten, email, email_chuan_hoa, so_dien_thoai, mat_khau_bam, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """).params(hoTen, email, emailChuanHoa, dienThoai, matKhauBam, luc, luc).update(khoa, "id");
        long id = khoa.getKey().longValue();
        int soDong = jdbc.sql("""
            INSERT INTO nguoi_dung_vai_tro (nguoi_dung_id, vai_tro_id)
            SELECT ?, id FROM vai_tro WHERE ma = 'USER'
            """).param(id).update();
        if (soDong != 1) {
            throw new IllegalStateException("Thiếu vai trò USER trong dữ liệu tham chiếu");
        }
        return id;
    }

    Optional<NguoiDung> timEmail(String emailChuanHoa) {
        return jdbc.sql("SELECT * FROM nguoi_dung WHERE email_chuan_hoa = ?")
            .param(emailChuanHoa).query(KhoTaiKhoan::docNguoiDung).optional();
    }

    Optional<NguoiDung> timDienThoai(String dienThoai) {
        return jdbc.sql("SELECT * FROM nguoi_dung WHERE so_dien_thoai = ?")
            .param(dienThoai).query(KhoTaiKhoan::docNguoiDung).optional();
    }

    Optional<NguoiDung> tim(long id) {
        return jdbc.sql("SELECT * FROM nguoi_dung WHERE id = ?")
            .param(id).query(KhoTaiKhoan::docNguoiDung).optional();
    }

    Optional<NguoiDung> khoaTheoId(long id) {
        return jdbc.sql("SELECT * FROM nguoi_dung WHERE id = ? FOR UPDATE")
            .param(id).query(KhoTaiKhoan::docNguoiDung).optional();
    }

    List<String> vaiTro(long id) {
        return jdbc.sql("""
            SELECT v.ma FROM vai_tro v JOIN nguoi_dung_vai_tro n ON n.vai_tro_id = v.id
            WHERE n.nguoi_dung_id = ? ORDER BY v.ma
            """).param(id).query(String.class).list();
    }

    void capNhatMatKhau(long id, String matKhauBam, LocalDateTime luc) {
        jdbc.sql("UPDATE nguoi_dung SET mat_khau_bam = ?, updated_at = ? WHERE id = ?")
            .params(matKhauBam, luc, id).update();
    }

    private static NguoiDung docNguoiDung(ResultSet rs, int thuTu) throws SQLException {
        return new NguoiDung(rs.getLong("id"), rs.getString("mat_khau_bam"),
            thoiGian(rs, "khoa_luc"), thoiGian(rs, "updated_at"));
    }

    private static LocalDateTime thoiGian(ResultSet rs, String cot) throws SQLException {
        return rs.getObject(cot, LocalDateTime.class);
    }
}
