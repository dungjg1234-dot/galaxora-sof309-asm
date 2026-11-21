package com.galaxora.nhiemvu;

import com.galaxora.quantri.NhatKyQuanTri;
import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.UUID;

import static com.galaxora.banhang.LoiBanHang.khongTimThay;

@RestController
class HoSoKhamPhaController {
    private final DichVuHoSoKhamPha hoSo;
    private final DichVuNhiemVu nhiemVu;
    private final QuanLyNhiemVu quanLy;
    private final NhatKyQuanTri nhatKy;

    HoSoKhamPhaController(DichVuHoSoKhamPha hoSo, DichVuNhiemVu nhiemVu, QuanLyNhiemVu quanLy, NhatKyQuanTri nhatKy) {
        this.hoSo = hoSo;
        this.nhiemVu = nhiemVu;
        this.quanLy = quanLy;
        this.nhatKy = nhatKy;
    }

    @GetMapping("/api/nhiem-vu/ho-so")
    DichVuHoSoKhamPha.HoSo xem(@AuthenticationPrincipal Phien phien) {
        return hoSo.xem(phien.nguoiDungId());
    }

    @GetMapping("/api/nhiem-vu/tien-do")
    DichVuNhiemVu.TienDo tienDo(@AuthenticationPrincipal Phien phien) {
        return nhiemVu.xem(phien.nguoiDungId());
    }

    @PostMapping("/api/nhiem-vu/kham-pha/{ma}/mo-bang")
    TuongTacKhamPha.LanMoBang moBang(@AuthenticationPrincipal Phien phien, @PathVariable String ma, HttpSession session) {
        if (DichVuNhiemVu.CAC_CHUONG.stream().noneMatch(c -> c.contains(ma))) throw khongTimThay();
        return TuongTacKhamPha.lay(session, phien.nguoiDungId()).moBang(ma, Instant.now());
    }

    @PostMapping("/api/nhiem-vu/kham-pha/{ma}")
    DichVuNhiemVu.TienDo khamPha(@AuthenticationPrincipal Phien phien, @PathVariable String ma, HttpSession session,
        @Valid @RequestBody XacNhanDocBang d) {
        TuongTacKhamPha.lay(session, phien.nguoiDungId()).xacNhanDocBang(ma, d.ve(), Instant.now());
        return nhiemVu.khamPha(phien.nguoiDungId(), ma);
    }

    record XacNhanDocBang(@NotNull UUID ve) {}
    record TraLoiTinHieu(@NotNull UUID ve, @NotNull @Positive Integer traLoi) {}

    @PostMapping("/api/nhiem-vu/dau-hieu")
    TuongTacKhamPha.TinHieu taoDauHieu(@AuthenticationPrincipal Phien phien, HttpSession session) {
        return TuongTacKhamPha.lay(session, phien.nguoiDungId()).moTinHieu(Instant.now());
    }

    @PostMapping("/api/nhiem-vu/vat-the-an")
    DichVuNhiemVu.TienDo vatTheAn(@AuthenticationPrincipal Phien phien, HttpSession session,
        @Valid @RequestBody TraLoiTinHieu d) {
        TuongTacKhamPha.lay(session, phien.nguoiDungId()).xacNhanTinHieu(d.ve(), d.traLoi(), Instant.now());
        return nhiemVu.vatTheAn(phien.nguoiDungId());
    }

    @PostMapping("/api/nhiem-vu/voucher")
    DichVuNhiemVu.TienDo voucher(@AuthenticationPrincipal Phien phien) {
        return nhiemVu.nhanVoucher(phien.nguoiDungId());
    }

    @GetMapping("/api/quan-tri/voucher-hanh-trinh")
    java.util.List<DichVuNhiemVu.CauHinh> mau() {
        return nhiemVu.cauHinh();
    }

    @PutMapping("/api/quan-tri/voucher-hanh-trinh")
    @Transactional
    ResponseEntity<Void> luu(@AuthenticationPrincipal Phien phien, @Valid @RequestBody DichVuNhiemVu.MauVoucher mau) {
        nhiemVu.luuVoucher(mau);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Đổi mẫu voucher hành trình", "/api/quan-tri/voucher-hanh-trinh");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/voucher-hanh-trinh")
    @Transactional
    ResponseEntity<Void> xoaMauVoucher(@AuthenticationPrincipal Phien phien) {
        nhiemVu.xoaMauVoucher();
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa mẫu voucher hành trình", "/api/quan-tri/voucher-hanh-trinh");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/nhiem-vu/doi-chieu")
    DichVuNhiemVu.TienDo doiChieu(@AuthenticationPrincipal Phien phien) {
        return nhiemVu.doiChieu(phien.nguoiDungId());
    }

    @GetMapping("/api/quan-tri/nhiem-vu")
    java.util.List<QuanLyNhiemVu.NhiemVu> danhSach() {
        return quanLy.danhSach();
    }

    record NhiemVuMoi(long id) {}

    @PostMapping("/api/quan-tri/nhiem-vu")
    @Transactional
    ResponseEntity<NhiemVuMoi> taoNhiemVu(@AuthenticationPrincipal Phien phien,
                                         @Valid @RequestBody QuanLyNhiemVu.Tao d) {
        long id = quanLy.tao(d);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Tạo nhiệm vụ", "/api/quan-tri/nhiem-vu/" + id);
        return ResponseEntity.status(201).body(new NhiemVuMoi(id));
    }

    @PutMapping("/api/quan-tri/nhiem-vu/{id}")
    @Transactional
    ResponseEntity<Void> suaNhiemVu(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                                    @Valid @RequestBody QuanLyNhiemVu.Sua d) {
        quanLy.sua(id, d);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Sửa nhiệm vụ", "/api/quan-tri/nhiem-vu/" + id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/nhiem-vu/{id}")
    @Transactional
    ResponseEntity<Void> xoaNhiemVu(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        quanLy.xoa(id);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa nhiệm vụ", "/api/quan-tri/nhiem-vu/" + id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/nhiem-vu/{id}/voucher")
    @Transactional
    ResponseEntity<Void> xoaVoucherNhiemVu(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        quanLy.xoaVoucher(id);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa mẫu voucher nhiệm vụ", "/api/quan-tri/nhiem-vu/" + id + "/voucher");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/quan-tri/nhiem-vu/{id}/voucher")
    @Transactional
    ResponseEntity<Void> luuVoucherNhiemVu(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                                           @Valid @RequestBody DichVuNhiemVu.MauVoucher mau) {
        quanLy.luuVoucher(id, mau);
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Đổi mẫu voucher nhiệm vụ", "/api/quan-tri/nhiem-vu/" + id + "/voucher");
        return ResponseEntity.noContent().build();
    }
}
