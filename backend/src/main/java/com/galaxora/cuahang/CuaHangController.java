package com.galaxora.cuahang;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.galaxora.quantri.NhatKyQuanTri;
import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@RestController
class CuaHangController {
    private final DichVuCuaHang cuaHang;
    private final KhoMoHinhSanPham moHinh;
    private final NhatKyQuanTri nhatKy;

    CuaHangController(DichVuCuaHang cuaHang, KhoMoHinhSanPham moHinh, NhatKyQuanTri nhatKy) {
        this.cuaHang = cuaHang;
        this.moHinh = moHinh;
        this.nhatKy = nhatKy;
    }

    record MaDinhDanh(long id) {}
    record GhiDanhMuc(@NotBlank @Size(max = 160) String ten, @NotNull Boolean hienThi) {}
    record Anh(@NotBlank @Size(max = 2048) String duongDan, @NotNull @Size(max = 250) String moTa) {}
    record GhiSanPham(
        @Positive long danhMucId, @NotBlank @Size(max = 64) String sku, @NotBlank @Size(max = 200) String ten,
        @NotNull @Size(max = 500) String moTaNgan, @NotNull @Size(max = 16000) String moTa,
        @NotNull @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal gia,
        @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giaSoSanh,
        @NotNull Boolean hienThi, @NotNull Boolean kinhDoanh,
        @NotNull @Size(max = 12) List<@NotNull @Valid Anh> hinhAnh) {}
    record GhiYeuThich(@NotNull Boolean yeuThich) {}
    record DuyetMoHinh(@NotNull Boolean daDuyet) {}

    @GetMapping("/api/cua-hang/danh-muc")
    List<DichVuCuaHang.DanhMuc> danhMuc() { return cuaHang.danhMuc(false); }

    @GetMapping("/api/quan-tri/cua-hang/danh-muc")
    List<DichVuCuaHang.DanhMuc> danhMucQuanTri() { return cuaHang.danhMuc(true); }

    @PostMapping("/api/quan-tri/cua-hang/danh-muc")
    @Transactional
    MaDinhDanh taoDanhMuc(@AuthenticationPrincipal Phien phien, @Valid @RequestBody GhiDanhMuc duLieu) {
        long id = cuaHang.luuDanhMuc(null, duLieu.ten(), duLieu.hienThi());
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Tạo danh mục", "/api/quan-tri/cua-hang/danh-muc/" + id);
        return new MaDinhDanh(id);
    }

    @PutMapping("/api/quan-tri/cua-hang/danh-muc/{id}")
    @Transactional
    MaDinhDanh suaDanhMuc(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                         @Valid @RequestBody GhiDanhMuc duLieu) {
        long daLuu = cuaHang.luuDanhMuc(id, duLieu.ten(), duLieu.hienThi());
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Sửa danh mục", "/api/quan-tri/cua-hang/danh-muc/" + id);
        return new MaDinhDanh(daLuu);
    }

    @DeleteMapping("/api/quan-tri/cua-hang/danh-muc/{id}")
    @Transactional
    ResponseEntity<Void> xoaDanhMuc(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        cuaHang.xoaDanhMuc(id);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa danh mục", "/api/quan-tri/cua-hang/danh-muc/" + id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/cua-hang/san-pham/{id}")
    @Transactional
    ResponseEntity<Void> xoaSanPham(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        cuaHang.xoaSanPham(id);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa sản phẩm", "/api/quan-tri/cua-hang/san-pham/" + id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/cua-hang/san-pham/{id}/mo-hinh")
    @Transactional
    ResponseEntity<Void> xoaMoHinh(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        moHinh.xoa(id);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa mô hình 3D", "/api/quan-tri/cua-hang/san-pham/" + id + "/mo-hinh");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/cua-hang/san-pham")
    DichVuCuaHang.Trang sanPham(@RequestParam(defaultValue = "") @Size(max = 160) String tuKhoa,
        @RequestParam(required = false) @Positive Long danhMucId, @RequestParam(defaultValue = "moi") String sapXep,
        @RequestParam(defaultValue = "0") @Min(0) int trang,
        @RequestParam(defaultValue = "12") @Min(1) @Max(100) int kichThuoc) {
        return cuaHang.tim(tuKhoa, danhMucId, sapXep, trang, kichThuoc, false);
    }

    @GetMapping("/api/quan-tri/cua-hang/san-pham")
    DichVuCuaHang.Trang sanPhamQuanTri(@RequestParam(defaultValue = "") @Size(max = 160) String tuKhoa,
        @RequestParam(defaultValue = "0") @Min(0) int trang,
        @RequestParam(defaultValue = "24") @Min(1) @Max(100) int kichThuoc) {
        return cuaHang.tim(tuKhoa, null, "moi", trang, kichThuoc, true);
    }

    @GetMapping("/api/cua-hang/san-pham/{id}")
    DichVuCuaHang.ChiTiet chiTiet(@PathVariable long id) { return cuaHang.chiTiet(id, false); }

    @GetMapping("/api/quan-tri/cua-hang/san-pham/{id}")
    DichVuCuaHang.ChiTiet chiTietQuanTri(@PathVariable long id) { return cuaHang.chiTiet(id, true); }

    @PostMapping("/api/quan-tri/cua-hang/san-pham")
    @Transactional
    MaDinhDanh taoSanPham(@AuthenticationPrincipal Phien phien, @Valid @RequestBody GhiSanPham duLieu) {
        long id = cuaHang.luuSanPham(null, duLieu);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Tạo sản phẩm", "/api/quan-tri/cua-hang/san-pham/" + id);
        return new MaDinhDanh(id);
    }

    @PutMapping("/api/quan-tri/cua-hang/san-pham/{id}")
    @Transactional
    MaDinhDanh suaSanPham(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                         @Valid @RequestBody GhiSanPham duLieu) {
        long daLuu = cuaHang.luuSanPham(id, duLieu);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), duLieu.hienThi() ? "Sửa sản phẩm" : "Lưu sản phẩm ở trạng thái ẩn",
            "/api/quan-tri/cua-hang/san-pham/" + id);
        return new MaDinhDanh(daLuu);
    }

    @GetMapping("/api/cua-hang/yeu-thich")
    List<DichVuCuaHang.SanPham> yeuThich(@AuthenticationPrincipal Phien phien) {
        return cuaHang.yeuThich(phien.nguoiDungId());
    }

    @PutMapping("/api/cua-hang/yeu-thich/{id}")
    ResponseEntity<Void> datYeuThich(@AuthenticationPrincipal Phien phien, @PathVariable long id,
        @Valid @RequestBody GhiYeuThich duLieu) {
        cuaHang.datYeuThich(phien.nguoiDungId(), id, duLieu.yeuThich());
        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/api/quan-tri/cua-hang/san-pham/{id}/mo-hinh", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    KhoMoHinhSanPham.ThongTin taiMoHinh(@AuthenticationPrincipal Phien phien, @PathVariable long id,
        @RequestParam MultipartFile tep,
        @RequestParam @NotBlank @Size(max = 255) String giayPhep,
        @RequestParam @NotBlank @Size(max = 2048) String nguon,
        @RequestParam @NotBlank @Size(max = 250) String tacGia,
        @RequestParam @NotBlank @Size(max = 500) String ghiCong) {
        var daTai = moHinh.taiLen(id, tep, giayPhep, nguon, tacGia, ghiCong);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Tải mô hình sản phẩm",
            "/api/quan-tri/cua-hang/san-pham/" + id + "/mo-hinh");
        return daTai;
    }

    @GetMapping("/api/quan-tri/cua-hang/san-pham/{id}/mo-hinh")
    KhoMoHinhSanPham.ThongTin thongTinQuanTri(@PathVariable long id) { return moHinh.thongTin(id, true); }

    @PutMapping("/api/quan-tri/cua-hang/san-pham/{id}/mo-hinh")
    @Transactional
    KhoMoHinhSanPham.ThongTin duyet(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                                    @Valid @RequestBody DuyetMoHinh duLieu) {
        var daLuu = moHinh.duyet(id, duLieu.daDuyet());
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), duLieu.daDuyet() ? "Duyệt mô hình sản phẩm" : "Ẩn mô hình sản phẩm",
            "/api/quan-tri/cua-hang/san-pham/" + id + "/mo-hinh");
        return daLuu;
    }

    @GetMapping("/api/cua-hang/san-pham/{id}/mo-hinh")
    KhoMoHinhSanPham.ThongTin thongTinMoHinh(@PathVariable long id) { return moHinh.thongTin(id, false); }

    @GetMapping("/api/cua-hang/san-pham/{id}/mo-hinh/noi-dung")
    ResponseEntity<byte[]> noiDungMoHinh(@PathVariable long id) {
        var tep = moHinh.noiDung(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(
            tep.dinhDang().equals("glb") ? "model/gltf-binary" : "model/gltf+json"))
            .cacheControl(CacheControl.noStore()).body(tep.duLieu());
    }
}
