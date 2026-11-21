package com.galaxora.cuahang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * Y3 - Module URL: Kiểm thử DichVuCuaHang (URL HTTPS, sắp xếp, bảo vệ xóa danh mục/SP) (TC-URL-01 -> TC-URL-08).
 */
@DisplayName("URL - Cửa hàng, kiểm tra URL ảnh và quy tắc danh mục (DichVuCuaHang)")
class DichVuCuaHangAsmTest {

    private JdbcTemplate jdbc;
    private DichVuCuaHang dichVu;

    @BeforeEach
    void thietLap() {
        jdbc = mock(JdbcTemplate.class);
        dichVu = new DichVuCuaHang(jdbc);
    }

    @ParameterizedTest(name = "TC-URL-01: URL HTTPS hợp lệ \"{0}\" được chấp nhận")
    @ValueSource(strings = {
        "https://cdn.galaxora.vn/anh/sp1.webp",
        "HTTPS://example.com/images/space.png?v=1"
    })
    void urlHttpsHopLeDuocChapNhan(String url) {
        assertThatCode(() -> DichVuCuaHang.kiemTraUrl(url)).doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "TC-URL-02: Giao thức không an toàn \"{0}\" bị chặn (400)")
    @ValueSource(strings = {
        "http://example.com/a.png",
        "javascript:alert(1)",
        "file:///etc/passwd",
        "ftp://files.example.com/a.png"
    })
    void giaoThucKhongAnToanBiChan(String url) {
        assertThatThrownBy(() -> DichVuCuaHang.kiemTraUrl(url))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("URL HTTPS hợp lệ");
            });
    }

    @Test
    @DisplayName("TC-URL-03: URL chứa userinfo (https://admin:123@host) bị chặn")
    void urlChuaUserInfoBiChan() {
        assertThatThrownBy(() -> DichVuCuaHang.kiemTraUrl("https://admin:123@example.com/a.png"))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @ParameterizedTest(name = "TC-URL-04: URL sai cú pháp hoặc thiếu host \"{0}\" bị chặn")
    @ValueSource(strings = {"https://", "khong phai url", ""})
    void urlSaiCuPhapBiChan(String url) {
        assertThatThrownBy(() -> DichVuCuaHang.kiemTraUrl(url))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("TC-URL-05: Tham số sắp xếp không thuộc whitelist (chống SQL injection) trả 400")
    void sapXepKhongHopLeTra400() {
        assertThatThrownBy(() -> dichVu.tim("", null, "gia_tang; DROP TABLE san_pham", 0, 12, false))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("Cách sắp xếp không hợp lệ");
            });
    }

    @Test
    @DisplayName("TC-URL-06: Xóa danh mục không tồn tại ném 404 Not Found")
    void xoaDanhMucKhongTonTaiTra404() {
        when(jdbc.queryForList(anyString(), eq(Long.class), eq(999L))).thenReturn(List.of());
        assertThatThrownBy(() -> dichVu.xoaDanhMuc(999L))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("TC-URL-07: Xóa danh mục vẫn còn sản phẩm chưa xóa ném 409 Conflict")
    void xoaDanhMucConSanPhamTra409() {
        when(jdbc.queryForList(anyString(), eq(Long.class), eq(10L)))
            .thenReturn(List.of(10L))
            .thenReturn(List.of(55L));
        assertThatThrownBy(() -> dichVu.xoaDanhMuc(10L))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(e.getReason()).contains("chuyển hoặc xóa các sản phẩm");
            });
    }

    @Test
    @DisplayName("TC-URL-08: Xóa sản phẩm không tồn tại (update = 0) ném 404 Not Found")
    void xoaSanPhamKhongTonTaiTra404() {
        when(jdbc.update(anyString(), any(Object[].class))).thenReturn(0);
        assertThatThrownBy(() -> dichVu.xoaSanPham(888L))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
