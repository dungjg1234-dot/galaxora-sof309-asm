package com.galaxora.taikhoan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Y3 - Module RLM: Kiểm thử giá trị biên bộ giới hạn đăng nhập GioiHanDangNhap (TC-RLM-01 -> TC-RLM-06).
 */
@DisplayName("RLM - Giới hạn đăng nhập chống brute-force (GioiHanDangNhap)")
class GioiHanDangNhapAsmTest {

    private GioiHanDangNhap gioiHan;

    @BeforeEach
    void thietLap() {
        gioiHan = new GioiHanDangNhap();
    }

    @Test
    @DisplayName("TC-RLM-01: Cho phép đúng 5 lần thử liên tiếp trên cùng một tài khoản")
    void choPhepDung5LanTheoTaiKhoan() {
        for (int i = 1; i <= 5; i++) {
            assertThat(gioiHan.nhanTheoTaiKhoan("id:10"))
                .as("Lần thử thứ " + i).isTrue();
        }
        assertThat(gioiHan.canhBaoGanDay().theoTaiKhoan()).isFalse();
    }

    @Test
    @DisplayName("TC-RLM-02: Lần thứ 6 cùng tài khoản bị chặn và bật cờ cảnh báo theoTaiKhoan")
    void lanThu6BiChanVaBatCanhBao() {
        for (int i = 0; i < 5; i++) {
            gioiHan.nhanTheoTaiKhoan("id:10");
        }
        assertThat(gioiHan.nhanTheoTaiKhoan("id:10")).isFalse();
        assertThat(gioiHan.canhBaoGanDay().theoTaiKhoan()).isTrue();
        assertThat(gioiHan.canhBaoGanDay().theoIp()).isFalse();
    }

    @Test
    @DisplayName("TC-RLM-03: Gọi xoaLoiTheoTaiKhoan sau khi đăng nhập đúng sẽ xóa bộ đếm lỗi")
    void xoaLoiTheoTaiKhoanResetBoDem() {
        for (int i = 0; i < 5; i++) {
            gioiHan.nhanTheoTaiKhoan("id:20");
        }
        assertThat(gioiHan.nhanTheoTaiKhoan("id:20")).isFalse();
        gioiHan.xoaLoiTheoTaiKhoan("id:20");
        assertThat(gioiHan.nhanTheoTaiKhoan("id:20")).isTrue();
    }

    @Test
    @DisplayName("TC-RLM-04: Biên 30 lần theo IP được phép, lần thứ 31 bị chặn và bật cảnh báo theoIp")
    void bien30LanTheoIpVaChanLan31() {
        for (int i = 1; i <= 30; i++) {
            assertThat(gioiHan.nhanTheoIp("203.0.113.10"))
                .as("Lần IP thứ " + i).isTrue();
        }
        assertThat(gioiHan.nhanTheoIp("203.0.113.10")).isFalse();
        assertThat(gioiHan.canhBaoGanDay().theoIp()).isTrue();
    }

    @Test
    @DisplayName("TC-RLM-05: Chặn tài khoản A không ảnh hưởng tới tài khoản B (cô lập khóa)")
    void coLapGioiHanGiuaCacTaiKhoan() {
        for (int i = 0; i < 6; i++) {
            gioiHan.nhanTheoTaiKhoan("id:100");
        }
        assertThat(gioiHan.nhanTheoTaiKhoan("id:100")).isFalse();
        assertThat(gioiHan.nhanTheoTaiKhoan("id:200")).isTrue();
    }

    @Test
    @DisplayName("TC-RLM-06: Trạng thái khởi tạo chưa có lượt chặn -> CanhBao(false, false)")
    void canhBaoMacDinhLaFalse() {
        assertThat(gioiHan.canhBaoGanDay()).isEqualTo(new GioiHanDangNhap.CanhBao(false, false));
    }
}
