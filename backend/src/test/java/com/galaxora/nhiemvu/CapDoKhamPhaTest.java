package com.galaxora.nhiemvu;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CapDoKhamPhaTest {
    @Test
    void dungTaiVaNgayTruocMocLenCap() {
        assertThat(CapDoKhamPha.tuTongXp(0)).isEqualTo(new CapDoKhamPha(1, 0, 100));
        assertThat(CapDoKhamPha.tuTongXp(99).cap()).isEqualTo(1);
        assertThat(CapDoKhamPha.tuTongXp(100)).isEqualTo(new CapDoKhamPha(2, 100, 250));
        assertThat(CapDoKhamPha.tuTongXp(249).cap()).isEqualTo(2);
        assertThat(CapDoKhamPha.tuTongXp(250)).isEqualTo(new CapDoKhamPha(3, 250, 450));
        assertThat(CapDoKhamPha.tuTongXp(450)).isEqualTo(new CapDoKhamPha(4, 450, 700));
        assertThat(CapDoKhamPha.tuTongXp(700)).isEqualTo(new CapDoKhamPha(5, 700, 1000));
        assertThat(CapDoKhamPha.tuTongXp(1000)).isEqualTo(new CapDoKhamPha(6, 1000, 1350));
    }
}
