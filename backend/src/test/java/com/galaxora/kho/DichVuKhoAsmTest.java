package com.galaxora.kho;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import com.galaxora.quantri.NhatKyQuanTri;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.server.ResponseStatusException;

/**
 * Y3 - Module KHO: Kiểm thử quy tắc tồn kho, biên tràn số và chống lặp phiếu (TC-KHO-01 -> TC-KHO-06).
 */
@DisplayName("KHO - Quản lý tồn kho và biến động kho (DichVuKho)")
class DichVuKhoAsmTest {

    private JdbcTemplate jdbc;
    private NhatKyQuanTri nhatKy;
    private DichVuKho dichVu;

    @BeforeEach
    void thietLap() {
        jdbc = mock(JdbcTemplate.class);
        nhatKy = mock(NhatKyQuanTri.class);
        dichVu = new DichVuKho(jdbc, nhatKy);
        when(jdbc.queryForObject(eq("SELECT id FROM nguoi_dung WHERE id=? FOR UPDATE"), eq(Long.class), any()))
            .thenReturn(1L);
    }

    @Test
    @DisplayName("TC-KHO-01: Điều chỉnh tồn kho với thayDoi = 0 bị từ chối (400)")
    void dieuChinhBangKhongBiTuChoi() {
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(), any())).thenReturn(0L);
        var dto = new KhoController.DieuChinh(UUID.randomUUID().toString(), 0, false, "Kiểm kê");
        assertThatThrownBy(() -> dichVu.dieuChinh(1L, 10L, dto))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("phải khác 0");
            });
    }

    @Test
    @DisplayName("TC-KHO-02: Xuất kho với thayDoi dương (+5) bị từ chối (400)")
    void xuatKhoSoDuongBiTuChoi() {
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(), any())).thenReturn(0L);
        var dto = new KhoController.DieuChinh(UUID.randomUUID().toString(), 5, true, "Xuất hủy");
        assertThatThrownBy(() -> dichVu.dieuChinh(1L, 10L, dto))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("xuất kho phải là số âm");
            });
    }

    @Test
    @DisplayName("TC-KHO-03: Xuất vượt tồn khả dụng (thực tế 5, giữ 3, xuất -3) trả 409 Conflict")
    @SuppressWarnings("unchecked")
    void xuatVuotTonKhaDungTra409() {
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(), any())).thenReturn(0L);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(10L))).thenReturn(List.<int[]>of(new int[]{5, 3}));
        var dto = new KhoController.DieuChinh(UUID.randomUUID().toString(), -3, true, "Xuất vượt tồn");
        assertThatThrownBy(() -> dichVu.dieuChinh(1L, 10L, dto))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(e.getReason()).contains("Không đủ tồn khả dụng");
            });
    }

    @Test
    @DisplayName("TC-KHO-04: Biên tràn số nguyên (Integer.MAX_VALUE + 1) trả 409 Conflict")
    @SuppressWarnings("unchecked")
    void bienTranSoNguyenTra409() {
        when(jdbc.queryForObject(anyString(), eq(Long.class), any(), any())).thenReturn(0L);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(10L)))
            .thenReturn(List.<int[]>of(new int[]{Integer.MAX_VALUE, 0}));
        var dto = new KhoController.DieuChinh(UUID.randomUUID().toString(), 1, false, "Cộng vượt giới hạn");
        assertThatThrownBy(() -> dichVu.dieuChinh(1L, 10L, dto))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    @DisplayName("TC-KHO-05: Phiếu nhập chứa 2 dòng trùng sanPhamId bị từ chối (400)")
    void phieuNhapTrungSanPhamTra400() {
        when(jdbc.queryForList(anyString(), eq(Long.class), any(), any())).thenReturn(List.of());
        var dto = new KhoController.NhapKho(UUID.randomUUID().toString(), "Nhập hàng đợt 1",
            List.of(new KhoController.DongNhap(10L, 5), new KhoController.DongNhap(10L, 3)));
        assertThatThrownBy(() -> dichVu.nhap(1L, dto))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("chỉ xuất hiện một lần");
            });
    }

    @Test
    @DisplayName("TC-KHO-06: Gửi lại phiếu nhập cùng khoaYeuCau (idempotency) trả ID phiếu cũ, không cộng tồn lại")
    void phieuNhapLapKhoaYeuCauTraIdCu() {
        String khoa = UUID.randomUUID().toString();
        when(jdbc.queryForList(anyString(), eq(Long.class), eq(khoa), eq(1L))).thenReturn(List.of(42L));
        var dto = new KhoController.NhapKho(khoa, "Nhập lại", List.of(new KhoController.DongNhap(10L, 5)));
        long id = dichVu.nhap(1L, dto);
        assertThat(id).isEqualTo(42L);
        verify(nhatKy, never()).ghiTrongGiaoDich(any(Long.class), anyString(), anyString());
    }
}
