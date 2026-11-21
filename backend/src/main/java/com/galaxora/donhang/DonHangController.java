package com.galaxora.donhang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.galaxora.quantri.NhatKyQuanTri;
import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
class DonHangController {
    private final DichVuGioHang gioHang;
    private final DichVuDonHang donHang;
    private final DichVuVoucher voucher;
    private final NhatKyQuanTri nhatKy;

    DonHangController(DichVuGioHang gioHang, DichVuDonHang donHang, DichVuVoucher voucher, NhatKyQuanTri nhatKy) {
        this.gioHang = gioHang;
        this.donHang = donHang;
        this.voucher = voucher;
        this.nhatKy = nhatKy;
    }

    record SoLuong(@NotNull @PositiveOrZero Integer soLuong) {}
    record ThemSoLuong(@Positive int soLuong) {}
    record Phi(@PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giaTri) {}
    record MaDinhDanh(long id) {}
    record DatDon(@NotNull @Pattern(regexp = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}") String khoaYeuCau,
        @NotBlank @Size(max = 160) String nguoiNhan, @NotBlank @Pattern(regexp = "\\+?[0-9]{8,15}") String soDienThoai,
        @NotBlank @Size(max = 500) String diaChi, @Size(max = 500) String ghiChu, @Size(max = 40) String maVoucher,
        @NotNull @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal tongTienDaXacNhan) {}
    record ChuyenTrangThai(@NotNull DichVuDonHang.TrangThai trangThai, boolean daThuTien, @Size(max = 500) String lyDo) {}
    record LyDo(@NotBlank @Size(max = 500) String lyDo) {}
    record XuLyHuy(@NotNull Boolean duyet, @NotBlank @Size(max = 500) String lyDo) {}
    record GhiVoucher(@NotBlank @Pattern(regexp = "[a-zA-Z0-9_-]{1,40}") String ma,
        @NotNull @Pattern(regexp = "PHAN_TRAM|SO_TIEN") String loai,
        @NotNull @Positive @Digits(integer = 13, fraction = 2) BigDecimal giaTri,
        @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal giamToiDa,
        @PositiveOrZero @Digits(integer = 13, fraction = 2) BigDecimal donToiThieu,
        @NotNull LocalDateTime batDau, @NotNull LocalDateTime ketThuc, @Positive int luotToiDa,
        @NotNull Boolean kichHoat, @Positive Long chuSoHuuId) {}

    @GetMapping("/api/gio-hang")
    DichVuGioHang.Gio xemGio(@AuthenticationPrincipal Phien phien) { return gioHang.xem(phien.nguoiDungId()); }

    @PutMapping("/api/gio-hang/{id}")
    ResponseEntity<Void> suaGio(@AuthenticationPrincipal Phien phien, @PathVariable long id,
        @Valid @RequestBody SoLuong d) {
        gioHang.sua(phien.nguoiDungId(), id, d.soLuong());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/gio-hang/{id}")
    ResponseEntity<Void> themGio(@AuthenticationPrincipal Phien phien, @PathVariable long id,
        @Valid @RequestBody ThemSoLuong d) {
        gioHang.them(phien.nguoiDungId(), id, d.soLuong());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/gio-hang/tinh-tien")
    DichVuDonHang.TongTien tinhTien(@AuthenticationPrincipal Phien phien,
        @RequestParam(required = false) @Size(max = 40) String maVoucher) {
        return donHang.baoGia(phien.nguoiDungId(), maVoucher);
    }

    @PostMapping("/api/don-hang")
    MaDinhDanh dat(@AuthenticationPrincipal Phien phien, @Valid @RequestBody DatDon d) {
        return new MaDinhDanh(donHang.dat(phien.nguoiDungId(), d));
    }

    @GetMapping("/api/don-hang")
    List<DichVuDonHang.Don> donCuaToi(@AuthenticationPrincipal Phien phien,
        @RequestParam(defaultValue = "0") @Min(0) int trang, @RequestParam(defaultValue = "20") @Min(1) @Max(100) int kichThuoc) {
        return donHang.danhSach(phien.nguoiDungId(), trang, kichThuoc);
    }

    @GetMapping("/api/don-hang/{id}")
    DichVuDonHang.ChiTiet chiTiet(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        return donHang.chiTiet(id, phien.nguoiDungId());
    }

    @PostMapping("/api/don-hang/{id}/huy")
    ResponseEntity<Void> huy(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        donHang.huyTruocXacNhan(id, phien.nguoiDungId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/don-hang/{id}/yeu-cau-huy")
    ResponseEntity<Void> yeuCauHuy(@AuthenticationPrincipal Phien phien, @PathVariable long id,
        @Valid @RequestBody LyDo d) {
        donHang.guiYeuCauHuy(id, phien.nguoiDungId(), d.lyDo());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/quan-tri/don-hang")
    List<DichVuDonHang.Don> donQuanTri(@RequestParam(defaultValue = "0") @Min(0) int trang,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int kichThuoc) {
        return donHang.danhSach(null, trang, kichThuoc);
    }

    @GetMapping("/api/quan-tri/don-hang/{id}")
    DichVuDonHang.ChiTiet chiTietQuanTri(@PathVariable long id) { return donHang.chiTiet(id, null); }

    @PostMapping("/api/quan-tri/don-hang/{id}/trang-thai")
    ResponseEntity<Void> chuyen(@AuthenticationPrincipal Phien phien, @PathVariable long id,
        @Valid @RequestBody ChuyenTrangThai d) {
        donHang.chuyen(id, phien.nguoiDungId(), d);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/quan-tri/don-hang/{id}/yeu-cau-huy/{yeuCauId}")
    ResponseEntity<Void> xuLy(@AuthenticationPrincipal Phien phien, @PathVariable long id, @PathVariable long yeuCauId,
        @Valid @RequestBody XuLyHuy d) {
        donHang.xuLyHuy(id, yeuCauId, phien.nguoiDungId(), d);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/quan-tri/don-hang/phi-van-chuyen")
    Phi phi() { return new Phi(donHang.phiVanChuyen()); }

    @PutMapping("/api/quan-tri/don-hang/phi-van-chuyen")
    @Transactional
    ResponseEntity<Void> datPhi(@AuthenticationPrincipal Phien phien, @Valid @RequestBody Phi d) {
        if (d.giaTri() == null) throw com.galaxora.banhang.LoiBanHang.khongHopLe("Cần nhập phí giao hàng.");
        donHang.datPhi(d.giaTri());
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Đổi phí vận chuyển COD", "/api/quan-tri/don-hang/phi-van-chuyen");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/quan-tri/voucher")
    List<DichVuVoucher.Voucher> voucher() { return voucher.danhSach(); }

    @PostMapping("/api/quan-tri/voucher")
    @Transactional
    ResponseEntity<Void> taoVoucher(@AuthenticationPrincipal Phien phien, @Valid @RequestBody GhiVoucher d) {
        voucher.luu(null, d);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Tạo voucher", "/api/quan-tri/voucher");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/quan-tri/voucher/{id}")
    @Transactional
    ResponseEntity<Void> suaVoucher(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                                    @Valid @RequestBody GhiVoucher d) {
        voucher.luu(id, d);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Sửa voucher", "/api/quan-tri/voucher/" + id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/voucher/{id}")
    @Transactional
    ResponseEntity<Void> xoaVoucher(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        voucher.xoa(id);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa voucher", "/api/quan-tri/voucher/" + id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/quan-tri/voucher/{id}/lich-su")
    List<DichVuVoucher.LanDung> lichSuVoucher(@PathVariable long id) { return voucher.lichSu(id); }
}
