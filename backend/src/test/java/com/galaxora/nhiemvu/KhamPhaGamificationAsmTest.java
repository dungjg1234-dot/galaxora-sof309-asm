package com.galaxora.nhiemvu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;

/**
 * Y3 - Module XP: Kiểm thử giá trị biên công thức lên cấp và cơ chế vé tương tác 3D (TC-XP-01 -> TC-XP-07).
 */
@DisplayName("XP - Cấp độ và tương tác khám phá vũ trụ 3D (CapDoKhamPha, TuongTacKhamPha)")
class KhamPhaGamificationAsmTest {

    private static final Instant T0 = Instant.parse("2026-11-21T08:00:00Z");

    @ParameterizedTest(name = "TC-XP-01: tongXp={0} -> cap={1} (moc {2}..{3})")
    @CsvSource({
        "0, 1, 0, 100",
        "99, 1, 0, 100",
        "100, 2, 100, 250",
        "249, 2, 100, 250",
        "250, 3, 250, 450",
        "449, 3, 250, 450",
        "450, 4, 450, 700",
        "1350, 7, 1350, 1750"
    })
    void mocCapDoKhamPhaTaiBien(long xp, long cap, long mocHienTai, long mocTiep) {
        assertThat(CapDoKhamPha.tuTongXp(xp)).isEqualTo(new CapDoKhamPha(cap, mocHienTai, mocTiep));
    }

    @Test
    @DisplayName("TC-XP-02: Tổng XP âm bất thường vẫn trả Cấp 1 (0..100) an toàn")
    void tongXpAmTraCap1() {
        assertThat(CapDoKhamPha.tuTongXp(-10)).isEqualTo(new CapDoKhamPha(1, 0, 100));
    }

    @Test
    @DisplayName("TC-XP-03: Biên đọc bảng thông tin thiên thể: 899ms bị chặn, đúng 900ms được chấp nhận")
    void bienDocBang899msVa900ms() {
        var tuongTac = TuongTacKhamPha.lay(new MockHttpSession(), 11L);
        var ve = tuongTac.moBang("saturn", T0);

        assertThatThrownBy(() -> tuongTac.xacNhanDocBang("saturn", ve.ve(), T0.plusMillis(899)))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(e.getReason()).contains("giữ bảng thông tin mở thêm");
            });

        tuongTac.xacNhanDocBang("saturn", ve.ve(), T0.plusMillis(900));
    }

    @Test
    @DisplayName("TC-XP-04: Biên hết hạn vé đọc bảng: 119.999s hợp lệ, đúng 120s hết hiệu lực")
    void bienHetHanVeDocBang120Giay() {
        var tuongTac = TuongTacKhamPha.lay(new MockHttpSession(), 12L);
        var ve1 = tuongTac.moBang("earth", T0);
        tuongTac.xacNhanDocBang("earth", ve1.ve(), T0.plusMillis(119_999));

        var ve2 = tuongTac.moBang("mars", T0);
        assertThatThrownBy(() -> tuongTac.xacNhanDocBang("mars", ve2.ve(), T0.plusSeconds(120)))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(e.getReason()).contains("hết hiệu lực");
            });
    }

    @Test
    @DisplayName("TC-XP-05: Tín hiệu vật thể ẩn sai 3 lần liên tiếp làm cạn lượt và chặn cả moTinHieu")
    void tinHieuSai3LanBiChan() {
        var tuongTac = TuongTacKhamPha.lay(new MockHttpSession(), 13L);
        var tinHieu = tuongTac.moTinHieu(T0);

        for (int conLai = 2; conLai >= 0; conLai--) {
            int kyVong = conLai;
            assertThatThrownBy(() -> tuongTac.xacNhanTinHieu(tinHieu.ve(), 999, T0.plusSeconds(1)))
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                    assertThat(e.getReason()).contains("Số lượt còn lại: " + kyVong);
                });
        }
        assertThatThrownBy(() -> tuongTac.moTinHieu(T0.plusSeconds(10)))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(e.getReason()).contains("Đã hết lượt trả lời");
            });
    }

    @Test
    @DisplayName("TC-XP-06: Sau khi hết hạn 300 giây, mở tín hiệu lại được cấp vé mới với 3 lượt")
    void moTinHieuMoiSau300Giay() {
        var tuongTac = TuongTacKhamPha.lay(new MockHttpSession(), 14L);
        var cu = tuongTac.moTinHieu(T0);
        for (int i = 0; i < 3; i++) {
            try {
                tuongTac.xacNhanTinHieu(cu.ve(), 999, T0.plusSeconds(1));
            } catch (ResponseStatusException ignored) {
            }
        }
        var moi = tuongTac.moTinHieu(T0.plusSeconds(300));
        assertThat(moi.ve()).isNotEqualTo(cu.ve());
        assertThat(moi.soLanConLai()).isEqualTo(3);
    }

    @Test
    @DisplayName("TC-XP-07: Đổi nguoiDungId trên cùng HttpSession sẽ tạo mới TuongTacKhamPha, cô lập vé")
    void coLapVeKhiDoiNguoiDungTrenCungSession() {
        var phien = new MockHttpSession();
        var ttUser1 = TuongTacKhamPha.lay(phien, 1L);
        var ve = ttUser1.moBang("venus", T0);

        var ttUser2 = TuongTacKhamPha.lay(phien, 2L);
        assertThatThrownBy(() -> ttUser2.xacNhanDocBang("venus", ve.ve(), T0.plusSeconds(2)))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }
}
