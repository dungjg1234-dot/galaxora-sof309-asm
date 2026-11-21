package com.galaxora.donhang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;

/**
 * Y3 - Module VCH: Kiểm thử bảng quyết định & biên Voucher và Giỏ hàng (TC-VCH-01 -> TC-VCH-10).
 */
@DisplayName("VCH - Tính giảm giá Voucher và tạm tính Giỏ hàng (DichVuVoucher, DichVuGioHang)")
class DichVuVoucherAsmTest {

    private JdbcTemplate jdbc;
    private DichVuVoucher dichVu;

    @BeforeEach
    void thietLap() {
        jdbc = mock(JdbcTemplate.class);
        dichVu = new DichVuVoucher(jdbc);
    }

    private DichVuVoucher.Voucher mauVoucher(String loai, String giaTri, String giamToiDa, String donToiThieu,
                                             int batDauOffsetGio, int ketThucOffsetGio,
                                             int luotToiDa, int luotDaDung, boolean kichHoat, Long chuSoHuu) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        return new DichVuVoucher.Voucher(
            1L, "GALA15", loai, new BigDecimal(giaTri),
            giamToiDa == null ? null : new BigDecimal(giamToiDa),
            donToiThieu == null ? null : new BigDecimal(donToiThieu),
            now.plusHours(batDauOffsetGio), now.plusHours(ketThucOffsetGio),
            luotToiDa, luotDaDung, kichHoat, chuSoHuu);
    }

    @SuppressWarnings("unchecked")
    private void giaLapVoucher(DichVuVoucher.Voucher v) {
        when(jdbc.query(anyString(), any(RowMapper.class), any(Object[].class))).thenReturn(List.of(v));
    }

    @Test
    @DisplayName("TC-VCH-01: Mã voucher null hoặc chỉ gồm khoảng trắng trả mức giảm 0 không truy vấn DB")
    void maVoucherRongTraKhong() {
        assertThat(dichVu.tinh(null, 1L, new BigDecimal("500000"), false).soTien()).isEqualByComparingTo("0");
        assertThat(dichVu.tinh("   ", 1L, new BigDecimal("500000"), false).soTien()).isEqualByComparingTo("0");
        verifyNoInteractions(jdbc);
    }

    @Test
    @DisplayName("TC-VCH-02: Bảng quyết định PHAN_TRAM 15% có trần giamToiDa 100.000")
    void voucherPhanTramCoTranGiamToiDa() {
        giaLapVoucher(mauVoucher("PHAN_TRAM", "15", "100000", null, -1, 24, 10, 0, true, null));
        var duoiTran = dichVu.tinh("gala15", 1L, new BigDecimal("500000"), false);
        assertThat(duoiTran.soTien()).isEqualByComparingTo("75000.00");

        var chamTran = dichVu.tinh("gala15", 1L, new BigDecimal("1000000"), false);
        assertThat(chamTran.soTien()).isEqualByComparingTo("100000");
    }

    @Test
    @DisplayName("TC-VCH-03: Voucher SO_TIEN lớn hơn tạm tính bị chặn bằng đúng tạm tính")
    void voucherSoTienKhongVuotTamTinh() {
        giaLapVoucher(mauVoucher("SO_TIEN", "200000", null, null, -1, 24, 10, 0, true, null));
        var ketQua = dichVu.tinh("GALA15", 1L, new BigDecimal("150000"), false);
        assertThat(ketQua.soTien()).isEqualByComparingTo("150000");
    }

    @Test
    @DisplayName("TC-VCH-04: Biên donToiThieu 300.000: 299.999 bị từ chối (409), 300.000 hợp lệ")
    void bienDonToiThieu() {
        giaLapVoucher(mauVoucher("SO_TIEN", "30000", null, "300000", -1, 24, 10, 0, true, null));
        assertThatThrownBy(() -> dichVu.tinh("GALA15", 1L, new BigDecimal("299999"), false))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

        var hopLe = dichVu.tinh("GALA15", 1L, new BigDecimal("300000"), false);
        assertThat(hopLe.soTien()).isEqualByComparingTo("30000");
    }

    @Test
    @DisplayName("TC-VCH-05: Voucher hết hạn, chưa đến thời điểm bắt đầu hoặc bị khóa đều trả 409")
    void voucherHetHanChuaBatDauHoacKhoaTra409() {
        var hetHan = mauVoucher("SO_TIEN", "10000", null, null, -5, -1, 10, 0, true, null);
        var chuaBatDau = mauVoucher("SO_TIEN", "10000", null, null, 2, 10, 10, 0, true, null);
        var daKhoa = mauVoucher("SO_TIEN", "10000", null, null, -1, 10, 10, 0, false, null);

        for (var v : List.of(hetHan, chuaBatDau, daKhoa)) {
            giaLapVoucher(v);
            assertThatThrownBy(() -> dichVu.tinh("GALA15", 1L, new BigDecimal("500000"), false))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                    e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
        }
    }

    @Test
    @DisplayName("TC-VCH-06: Voucher đã dùng hết lượt (luotDaDung == luotToiDa) trả 409")
    void voucherHetLuotTra409() {
        giaLapVoucher(mauVoucher("SO_TIEN", "20000", null, null, -1, 24, 10, 10, true, null));
        assertThatThrownBy(() -> dichVu.tinh("GALA15", 1L, new BigDecimal("500000"), false))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    @DisplayName("TC-VCH-07: Voucher cá nhân (chuSoHuuId=5) không thể dùng bởi người dùng khác (id=9)")
    void voucherCaNhanKhongDungDuocChoNguoiKhac() {
        giaLapVoucher(mauVoucher("SO_TIEN", "50000", null, null, -1, 24, 1, 0, true, 5L));
        assertThatThrownBy(() -> dichVu.tinh("GALA15", 9L, new BigDecimal("500000"), false))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));

        var dungChu = dichVu.tinh("GALA15", 5L, new BigDecimal("500000"), false);
        assertThat(dungChu.soTien()).isEqualByComparingTo("50000");
    }

    @Test
    @DisplayName("TC-VCH-08: Lưu voucher với ketThuc <= batDau hoặc PHAN_TRAM > 100% bị từ chối (400)")
    void luuVoucherSaiThoiHanHoacVuot100PhanTram() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        var saiThoiHan = new DonHangController.GhiVoucher("TEST1", "SO_TIEN", new BigDecimal("10000"),
            null, null, now, now, 10, true, null);
        var vuot100 = new DonHangController.GhiVoucher("TEST2", "PHAN_TRAM", new BigDecimal("100.01"),
            null, null, now, now.plusDays(1), 10, true, null);

        for (var dto : List.of(saiThoiHan, vuot100)) {
            assertThatThrownBy(() -> dichVu.luu(null, dto))
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(e.getReason()).contains("thời hạn và tỷ lệ giảm");
                });
        }
    }

    @Test
    @DisplayName("TC-VCH-09: Lưu voucher mới chuẩn hóa mã thành chữ in hoa và cắt khoảng trắng")
    void luuVoucherChuanHoaMaInHoa() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        var dto = new DonHangController.GhiVoucher("  galaxora10  ", "PHAN_TRAM", new BigDecimal("10"),
            new BigDecimal("50000"), new BigDecimal("100000"), now, now.plusDays(7), 100, true, null);
        dichVu.luu(null, dto);
        verify(jdbc).update(anyString(), eq("GALAXORA10"), eq("PHAN_TRAM"), eq(new BigDecimal("10")),
            eq(new BigDecimal("50000")), eq(new BigDecimal("100000")), eq(now), eq(now.plusDays(7)),
            eq(100), eq(true), eq(null));
    }

    @Test
    @DisplayName("TC-VCH-10: DichVuGioHang.tinhTamTinh tính chính xác bằng BigDecimal cho giỏ rỗng và nhiều dòng")
    void tinhTamTinhGioHangChinhXacBigDecimal() {
        assertThat(DichVuGioHang.tinhTamTinh(List.of())).isEqualByComparingTo("0");
        var dong = List.of(
            new DichVuGioHang.Dong(1L, "SKU1", "Mô hình Saturn", new BigDecimal("199000.50"), 2, 10, true, null),
            new DichVuGioHang.Dong(2L, "SKU2", "Đèn Hologram", new BigDecimal("50000"), 3, 10, true, null)
        );
        assertThat(DichVuGioHang.tinhTamTinh(dong)).isEqualByComparingTo("548001.00");
    }
}
