package com.galaxora.cuahang;

import static com.galaxora.banhang.LoiBanHang.*;

import java.time.LocalDateTime;
import java.util.List;

import com.galaxora.quantri.NhatKyQuanTri;
import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
class DanhGiaController {
    private final JdbcTemplate jdbc;
    private final DichVuCuaHang cuaHang;
    private final NhatKyQuanTri nhatKy;

    DanhGiaController(JdbcTemplate jdbc, DichVuCuaHang cuaHang, NhatKyQuanTri nhatKy) {
        this.jdbc = jdbc;
        this.cuaHang = cuaHang;
        this.nhatKy = nhatKy;
    }

    record GhiDanhGia(@Min(1) @Max(5) int soSao, @NotBlank @Size(max = 2000) String noiDung) {}
    record HienThi(@NotNull Boolean hienThi) {}
    record DanhGia(Long donHangId, Long sanPhamId, String tacGia, int soSao, String noiDung, boolean hienThi,
                   LocalDateTime capNhatLuc) {}

    @GetMapping("/api/cua-hang/san-pham/{id}/danh-gia")
    List<DanhGia> congKhai(@PathVariable long id) {
        cuaHang.laySanPham(id, false);
        return danhSach(id, false);
    }

    @GetMapping("/api/quan-tri/cua-hang/san-pham/{id}/danh-gia")
    List<DanhGia> quanTri(@PathVariable long id) {
        cuaHang.laySanPham(id, true);
        return danhSach(id, true);
    }

    @GetMapping("/api/danh-gia/{donHangId}")
    List<DanhGia> cuaToi(@AuthenticationPrincipal Phien phien, @PathVariable long donHangId) {
        return jdbc.query("""
            SELECT don_hang_id,san_pham_id,so_sao,noi_dung,hien_thi,updated_at FROM danh_gia
            WHERE don_hang_id=? AND nguoi_dung_id=? ORDER BY san_pham_id
            """, (r, i) -> new DanhGia(r.getLong(1), r.getLong(2), null, r.getInt(3),
                r.getString(4), r.getBoolean(5), r.getObject(6, LocalDateTime.class)), donHangId, phien.nguoiDungId());
    }

    @PutMapping("/api/danh-gia/{donHangId}/{sanPhamId}")
    @Transactional
    ResponseEntity<Void> luu(@AuthenticationPrincipal Phien phien, @PathVariable long donHangId,
        @PathVariable long sanPhamId, @Valid @RequestBody GhiDanhGia d) {
        long nguoiDungId = phien.nguoiDungId();
        var hopLe = jdbc.queryForList("""
            SELECT h.id FROM don_hang h JOIN chi_tiet_don_hang c ON c.don_hang_id=h.id
            WHERE h.id=? AND h.nguoi_dung_id=? AND c.san_pham_id=? AND h.trang_thai='DA_GIAO_THANH_CONG' FOR UPDATE
            """, Long.class, donHangId, nguoiDungId, sanPhamId);
        if (hopLe.isEmpty()) throw xungDot("Chỉ đánh giá sản phẩm trong đơn của bạn đã giao thành công.");
        var cu = jdbc.query("""
            SELECT so_sao,noi_dung FROM danh_gia WHERE don_hang_id=? AND san_pham_id=? AND nguoi_dung_id=? FOR UPDATE
            """, (r, i) -> new GhiDanhGia(r.getInt(1), r.getString(2)), donHangId, sanPhamId, nguoiDungId);
        String noiDung = d.noiDung().strip();
        if (cu.isEmpty()) {
            jdbc.update("""
                INSERT INTO danh_gia (don_hang_id,san_pham_id,nguoi_dung_id,so_sao,noi_dung) VALUES (?,?,?,?,?)
                """, donHangId, sanPhamId, nguoiDungId, d.soSao(), noiDung);
        } else if (cu.getFirst().soSao() != d.soSao() || !cu.getFirst().noiDung().equals(noiDung)) {
            jdbc.update("""
                INSERT INTO lich_su_danh_gia (don_hang_id,san_pham_id,nguoi_dung_id,so_sao,noi_dung)
                VALUES (?,?,?,?,?)
                """, donHangId, sanPhamId, nguoiDungId, cu.getFirst().soSao(), cu.getFirst().noiDung());
            jdbc.update("""
                UPDATE danh_gia SET so_sao=?,noi_dung=? WHERE don_hang_id=? AND san_pham_id=? AND nguoi_dung_id=?
                """, d.soSao(), noiDung, donHangId, sanPhamId, nguoiDungId);
        }
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/quan-tri/cua-hang/danh-gia/{donHangId}/{sanPhamId}/hien-thi")
    @Transactional
    ResponseEntity<Void> hienThi(@AuthenticationPrincipal Phien phien, @PathVariable long donHangId,
        @PathVariable long sanPhamId,
        @Valid @RequestBody HienThi d) {
        if (jdbc.update("UPDATE danh_gia SET hien_thi=? WHERE don_hang_id=? AND san_pham_id=?",
            d.hienThi(), donHangId, sanPhamId) == 0) throw khongTimThay();
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), d.hienThi() ? "Hiện đánh giá" : "Ẩn đánh giá",
            "/api/quan-tri/cua-hang/danh-gia/" + donHangId + "/" + sanPhamId + "/hien-thi");
        return ResponseEntity.noContent().build();
    }

    private List<DanhGia> danhSach(long id, boolean quanTri) {
        return jdbc.query("""
            SELECT g.don_hang_id,g.san_pham_id,n.ho_ten,g.so_sao,g.noi_dung,g.hien_thi,g.updated_at
            FROM danh_gia g JOIN nguoi_dung n ON n.id=g.nguoi_dung_id
            WHERE g.san_pham_id=? AND (? OR g.hien_thi) ORDER BY g.updated_at DESC,g.don_hang_id DESC
            """, (r, i) -> new DanhGia(quanTri ? r.getLong(1) : null, r.getLong(2), r.getString(3), r.getInt(4),
                r.getString(5), r.getBoolean(6), r.getObject(7, LocalDateTime.class)), id, quanTri);
    }
}
