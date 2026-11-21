package com.galaxora.taikhoan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Y3 - Module ACC: Kiểm thử đơn vị DichVuTaiKhoan với JUnit 5 & Mockito (TC-ACC-01 -> TC-ACC-12).
 */
@DisplayName("ACC - Tài khoản và xác thực (DichVuTaiKhoan)")
class DichVuTaiKhoanAsmTest {

    private KhoTaiKhoan kho;
    private GioiHanDangNhap gioiHan;
    private BCryptPasswordEncoder bcrypt;
    private DichVuTaiKhoan dichVu;

    @BeforeEach
    void thietLap() {
        kho = mock(KhoTaiKhoan.class);
        gioiHan = mock(GioiHanDangNhap.class);
        bcrypt = new BCryptPasswordEncoder(4);
        dichVu = new DichVuTaiKhoan(kho, gioiHan, bcrypt);
    }

    @Test
    @DisplayName("TC-ACC-01: Chuẩn hóa email cắt khoảng trắng và chuyển chữ thường")
    void chuanHoaEmailCatKhoangTrangVaThuong() {
        assertThat(DichVuTaiKhoan.chuanHoaEmail("  Dung.LH@Galaxora.VN  "))
            .isEqualTo("dung.lh@galaxora.vn");
    }

    @ParameterizedTest(name = "TC-ACC-02: SĐT hợp lệ \"{0}\" -> \"{1}\"")
    @CsvSource({
        "'+84 (912) 345-678', '+84912345678'",
        "'+1 202-555-0147', '+12025550147'",
        "'  +81 90-1234-5678  ', '+819012345678'"
    })
    void chuanHoaDienThoaiHopLe(String dauVao, String mongDoi) {
        assertThat(DichVuTaiKhoan.chuanHoaDienThoai(dauVao)).isEqualTo(mongDoi);
    }

    @ParameterizedTest(name = "TC-ACC-03: SĐT không đạt E.164 \"{0}\" -> chuỗi rỗng")
    @ValueSource(strings = {
        "0912345678",
        "+012345678",
        "+1",
        "+1234567890123456",
        "khong-phai-so",
        "   "
    })
    void chuanHoaDienThoaiKhongHopLe(String dauVao) {
        assertThat(DichVuTaiKhoan.chuanHoaDienThoai(dauVao)).isEmpty();
    }

    @ParameterizedTest(name = "TC-ACC-04: Mật khẩu rỗng hoặc toàn khoảng trắng bị từ chối")
    @ValueSource(strings = {"", "     ", "\t\n"})
    void matKhauRongBiTuChoi(String matKhau) {
        assertThat(DichVuTaiKhoan.matKhauHopLe(matKhau)).isFalse();
    }

    @Test
    @DisplayName("TC-ACC-05: Biên mật khẩu 72 byte hợp lệ, 73 byte và 25 chữ 'ế' (75 byte UTF-8) bị từ chối")
    void bienMatKhau72Va73ByteUtf8() {
        String dung72 = "a".repeat(72);
        String vuot73 = "a".repeat(73);
        String tiengViet75Byte = "ế".repeat(25);
        assertThat(tiengViet75Byte.getBytes(StandardCharsets.UTF_8)).hasSize(75);
        assertThat(DichVuTaiKhoan.matKhauHopLe(dung72)).isTrue();
        assertThat(DichVuTaiKhoan.matKhauHopLe(vuot73)).isFalse();
        assertThat(DichVuTaiKhoan.matKhauHopLe(tiengViet75Byte)).isFalse();
    }

    @Test
    @DisplayName("TC-ACC-06: Biên trên UTF-8 đúng 24 chữ 'ế' (72 byte) được chấp nhận")
    void bienMatKhau24ChuTiengViet72ByteHopLe() {
        String tiengViet72Byte = "ế".repeat(24);
        assertThat(tiengViet72Byte.getBytes(StandardCharsets.UTF_8)).hasSize(72);
        assertThat(DichVuTaiKhoan.matKhauHopLe(tiengViet72Byte)).isTrue();
    }

    @Test
    @DisplayName("TC-ACC-07: Đăng ký băm BCrypt mật khẩu trước khi gọi KhoTaiKhoan.taoNguoiDung")
    void dangKyBamMatKhauBCrypt() {
        dichVu.dangKy("Lương Hoàng Dũng", "dung@galaxora.vn", "dung@galaxora.vn", "+84912345678", "MatKhau@123");
        var captor = ArgumentCaptor.forClass(String.class);
        verify(kho).taoNguoiDung(eq("Lương Hoàng Dũng"), eq("dung@galaxora.vn"),
            eq("dung@galaxora.vn"), eq("+84912345678"), captor.capture(), any(LocalDateTime.class));
        assertThat(captor.getValue()).isNotEqualTo("MatKhau@123");
        assertThat(bcrypt.matches("MatKhau@123", captor.getValue())).isTrue();
    }

    @Test
    @DisplayName("TC-ACC-08: Đăng nhập đúng trả Phien và xóa bộ đếm lỗi tài khoản")
    void dangNhapThanhCongTraPhienVaXoaLoi() {
        LocalDateTime luc = LocalDateTime.of(2026, 11, 21, 8, 0);
        String bam = bcrypt.encode("MatKhauDung1");
        when(kho.timEmail("user@galaxora.vn")).thenReturn(Optional.of(new KhoTaiKhoan.NguoiDung(7L, bam, null, luc)));
        when(gioiHan.nhanTheoTaiKhoan("id:7")).thenReturn(true);
        when(kho.vaiTro(7L)).thenReturn(List.of("USER"));

        var ketQua = dichVu.dangNhap("user@galaxora.vn", "MatKhauDung1");
        assertThat(ketQua).isPresent();
        assertThat(ketQua.get().nguoiDungId()).isEqualTo(7L);
        assertThat(ketQua.get().vaiTro()).containsExactly("USER");
        verify(gioiHan).xoaLoiTheoTaiKhoan("id:7");
    }

    @Test
    @DisplayName("TC-ACC-09: Tài khoản bị khóa không đăng nhập được dù nhập đúng mật khẩu")
    void taiKhoanBiKhoaKhongDangNhapDuoc() {
        LocalDateTime luc = LocalDateTime.of(2026, 11, 21, 8, 0);
        String bam = bcrypt.encode("MatKhauDung1");
        when(kho.timEmail("locked@galaxora.vn")).thenReturn(
            Optional.of(new KhoTaiKhoan.NguoiDung(8L, bam, luc, luc)));
        when(gioiHan.nhanTheoTaiKhoan("id:8")).thenReturn(true);

        assertThat(dichVu.dangNhap("locked@galaxora.vn", "MatKhauDung1")).isEmpty();
        verify(gioiHan, never()).xoaLoiTheoTaiKhoan(any());
    }

    @Test
    @DisplayName("TC-ACC-10: Email không tồn tại trả Optional.empty() không ném lỗi")
    void emailKhongTonTaiTraEmpty() {
        when(kho.timEmail("la@galaxora.vn")).thenReturn(Optional.empty());
        when(gioiHan.nhanTheoTaiKhoan("khach:la@galaxora.vn")).thenReturn(true);

        assertThat(dichVu.dangNhap("la@galaxora.vn", "batky123")).isEmpty();
        verify(gioiHan, never()).xoaLoiTheoTaiKhoan(any());
    }

    @Test
    @DisplayName("TC-ACC-11: Phiên mất hiệu lực khi tài khoản bị khóa, đổi updatedAt hoặc đổi vai trò")
    void phienMatHieuLucKhiTrangThaiThayDoi() {
        LocalDateTime t1 = LocalDateTime.of(2026, 11, 21, 8, 0);
        LocalDateTime t2 = LocalDateTime.of(2026, 11, 21, 9, 0);
        var phien = new DichVuTaiKhoan.Phien(5L, t1, List.of("USER"));

        // Trường hợp 1: tài khoản bị khóa
        when(kho.tim(5L)).thenReturn(Optional.of(new KhoTaiKhoan.NguoiDung(5L, "h", t2, t1)));
        when(kho.vaiTro(5L)).thenReturn(List.of("USER"));
        assertThat(dichVu.phienConHieuLuc(phien)).isFalse();

        // Trường hợp 2: capNhatLuc đổi (sau khi đổi mật khẩu)
        when(kho.tim(5L)).thenReturn(Optional.of(new KhoTaiKhoan.NguoiDung(5L, "h", null, t2)));
        assertThat(dichVu.phienConHieuLuc(phien)).isFalse();

        // Trường hợp 3: vai trò thay đổi
        when(kho.tim(5L)).thenReturn(Optional.of(new KhoTaiKhoan.NguoiDung(5L, "h", null, t1)));
        when(kho.vaiTro(5L)).thenReturn(List.of("ADMIN", "USER"));
        assertThat(dichVu.phienConHieuLuc(phien)).isFalse();
    }

    @Test
    @DisplayName("TC-ACC-12: Đổi mật khẩu từ chối mật khẩu cũ sai / mật khẩu mới > 72 byte và cập nhật khi hợp lệ")
    void doiMatKhauKiemTraDieuKienVaCapNhat() {
        LocalDateTime t1 = LocalDateTime.of(2026, 11, 21, 8, 0);
        String bamCu = bcrypt.encode("Cu123456");
        var phien = new DichVuTaiKhoan.Phien(9L, t1, List.of("USER"));
        when(kho.khoaTheoId(9L)).thenReturn(Optional.of(new KhoTaiKhoan.NguoiDung(9L, bamCu, null, t1)));

        assertThat(dichVu.doiMatKhau(phien, "SaiMatKhau", "Moi123456")).isFalse();
        assertThat(dichVu.doiMatKhau(phien, "Cu123456", "a".repeat(73))).isFalse();
        verify(kho, never()).capNhatMatKhau(eq(9L), any(), any());

        assertThat(dichVu.doiMatKhau(phien, "Cu123456", "MoiHopLe@2026")).isTrue();
        var bamMoi = ArgumentCaptor.forClass(String.class);
        verify(kho).capNhatMatKhau(eq(9L), bamMoi.capture(), any(LocalDateTime.class));
        assertThat(bcrypt.matches("MoiHopLe@2026", bamMoi.getValue())).isTrue();
    }
}
