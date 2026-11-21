package com.galaxora.nhiemvu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.server.ResponseStatusException;

class TuongTacKhamPhaTest {
    private static final Instant LUC_MO = Instant.parse("2026-11-19T00:00:00Z");

    @Test
    void veDocBangDungMotLanChoDungThienTheVaPhien() {
        var phien = new MockHttpSession();
        var tuongTac = TuongTacKhamPha.lay(phien, 1);
        var ve = tuongTac.moBang("earth", LUC_MO);
        var lucHopLe = LUC_MO.plusMillis(ve.choMiliGiay());
        assertThatThrownBy(() -> tuongTac.xacNhanDocBang("earth", ve.ve(), LUC_MO))
            .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> TuongTacKhamPha.lay(new MockHttpSession(), 1).xacNhanDocBang("earth", ve.ve(), lucHopLe))
            .isInstanceOf(ResponseStatusException.class);
        assertThatThrownBy(() -> tuongTac.xacNhanDocBang("mars", ve.ve(), lucHopLe))
            .isInstanceOf(ResponseStatusException.class);
        tuongTac.xacNhanDocBang("earth", ve.ve(), lucHopLe);
        assertThatThrownBy(() -> tuongTac.xacNhanDocBang("earth", ve.ve(), lucHopLe))
            .isInstanceOf(ResponseStatusException.class);
        var veCu = tuongTac.moBang("earth", LUC_MO);
        assertThatThrownBy(() -> tuongTac.xacNhanDocBang("earth", veCu.ve(), LUC_MO.plusSeconds(120)))
            .isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void cauHoiTinHieuKiemTraDapAnVaGiuSoLanThuKhiMoLai() {
        var tuongTac = TuongTacKhamPha.lay(new MockHttpSession(), 1);
        var tinHieu = tuongTac.moTinHieu(LUC_MO);
        assertThatThrownBy(() -> tuongTac.xacNhanTinHieu(tinHieu.ve(), 99, LUC_MO))
            .isInstanceOf(ResponseStatusException.class);
        assertThat(tuongTac.moTinHieu(LUC_MO).ve()).isEqualTo(tinHieu.ve());
        assertThat(tuongTac.moTinHieu(LUC_MO).soLanConLai()).isEqualTo(2);
        int dung = switch (tinHieu.cauHoi()) {
            case "Trái Đất là hành tinh thứ mấy tính từ Mặt Trời?" -> 3;
            case "Sao Hỏa là hành tinh thứ mấy tính từ Mặt Trời?" -> 4;
            case "Mặt Trời tự quay khoảng bao nhiêu ngày tại xích đạo?" -> 25;
            default -> 8;
        };
        tuongTac.xacNhanTinHieu(tinHieu.ve(), dung, LUC_MO);
        assertThatThrownBy(() -> tuongTac.xacNhanTinHieu(tinHieu.ve(), dung, LUC_MO))
            .isInstanceOf(ResponseStatusException.class);
    }
}
