package com.galaxora.kho;

import java.util.List;

import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quan-tri/kho")
class KhoController {
    static final String DINH_DANG_KHOA = "[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}";
    private final DichVuKho kho;

    KhoController(DichVuKho kho) { this.kho = kho; }

    record DongNhap(@Positive long sanPhamId, @Positive int soLuong) {}
    record NhapKho(@NotNull @Pattern(regexp = DINH_DANG_KHOA) String khoaYeuCau, @NotBlank @Size(max = 500) String lyDo,
                   @NotEmpty @Size(max = 100) List<@NotNull @Valid DongNhap> chiTiet) {}
    record DieuChinh(@NotNull @Pattern(regexp = DINH_DANG_KHOA) String khoaYeuCau, int thayDoi,
                     boolean xuatKho, @NotBlank @Size(max = 500) String lyDo) {}
    record Nguong(@PositiveOrZero Integer giaTri) {}
    record MaDinhDanh(long id) {}

    @GetMapping("/ton")
    List<DichVuKho.TonKho> tonKho() { return kho.tonKho(); }

    @GetMapping("/ton/{id}/lich-su")
    List<DichVuKho.GiaoDich> lichSu(@PathVariable long id) { return kho.lichSu(id); }

    @GetMapping("/nguong")
    Nguong nguong() { return new Nguong(kho.nguong()); }

    @PutMapping("/nguong")
    ResponseEntity<Void> datNguong(@AuthenticationPrincipal Phien phien, @Valid @RequestBody Nguong duLieu) {
        if (duLieu.giaTri() == null) throw com.galaxora.banhang.LoiBanHang.khongHopLe("Cần nhập ngưỡng tồn kho.");
        kho.datNguong(phien.nguoiDungId(), duLieu.giaTri());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/phieu-nhap")
    List<DichVuKho.Phieu> phieuNhap() { return kho.phieuNhap(); }

    @GetMapping("/phieu-nhap/{id}")
    DichVuKho.ChiTietPhieu chiTietPhieu(@PathVariable long id) { return kho.chiTietPhieu(id); }

    @PostMapping("/phieu-nhap")
    MaDinhDanh nhap(@AuthenticationPrincipal Phien phien, @Valid @RequestBody NhapKho duLieu) {
        return new MaDinhDanh(kho.nhap(phien.nguoiDungId(), duLieu));
    }

    @PostMapping("/ton/{id}/dieu-chinh")
    ResponseEntity<Void> dieuChinh(@AuthenticationPrincipal Phien phien, @PathVariable long id,
        @Valid @RequestBody DieuChinh duLieu) {
        kho.dieuChinh(phien.nguoiDungId(), id, duLieu);
        return ResponseEntity.noContent().build();
    }
}
