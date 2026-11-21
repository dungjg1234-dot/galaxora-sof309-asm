package com.galaxora.taikhoan;

import static com.galaxora.banhang.LoiBanHang.khongTimThay;

import java.util.List;

import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tai-khoan/dia-chi")
class DiaChiController {
    private final JdbcTemplate jdbc;

    DiaChiController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    record DiaChi(long id, String nguoiNhan, String soDienThoai, String diaChi) {}
    record LuuDiaChi(@NotBlank @Size(max = 160) String nguoiNhan,
        @NotBlank @Pattern(regexp = "\\+?[0-9]{8,15}") String soDienThoai,
        @NotBlank @Size(max = 500) String diaChi) {}

    @GetMapping
    List<DiaChi> danhSach(@AuthenticationPrincipal Phien phien) {
        return jdbc.query("""
            SELECT id, nguoi_nhan, so_dien_thoai, dia_chi FROM dia_chi_nguoi_dung
            WHERE nguoi_dung_id=? ORDER BY id DESC
            """, (r, i) -> new DiaChi(r.getLong(1), r.getString(2), r.getString(3), r.getString(4)), phien.nguoiDungId());
    }

    @PostMapping
    ResponseEntity<Void> tao(@AuthenticationPrincipal Phien phien, @Valid @RequestBody LuuDiaChi d) {
        jdbc.update("""
            INSERT INTO dia_chi_nguoi_dung(nguoi_dung_id,nguoi_nhan,so_dien_thoai,dia_chi) VALUES (?,?,?,?)
            """, phien.nguoiDungId(), d.nguoiNhan().strip(), d.soDienThoai(), d.diaChi().strip());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    ResponseEntity<Void> sua(@AuthenticationPrincipal Phien phien, @PathVariable long id, @Valid @RequestBody LuuDiaChi d) {
        if (jdbc.update("""
            UPDATE dia_chi_nguoi_dung SET nguoi_nhan=?,so_dien_thoai=?,dia_chi=?
            WHERE id=? AND nguoi_dung_id=?
            """, d.nguoiNhan().strip(), d.soDienThoai(), d.diaChi().strip(), id, phien.nguoiDungId()) == 0) {
            throw khongTimThay();
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> xoa(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        if (jdbc.update("DELETE FROM dia_chi_nguoi_dung WHERE id=? AND nguoi_dung_id=?", id, phien.nguoiDungId()) == 0) {
            throw khongTimThay();
        }
        return ResponseEntity.noContent().build();
    }
}
