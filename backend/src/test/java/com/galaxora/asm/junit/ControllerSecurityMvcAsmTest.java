package com.galaxora.asm.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/**
 * Y3 - Module CTL: Kiểm thử tích hợp Controller, Spring Security, CSRF, Phân quyền,
 * Sổ địa chỉ, Đánh giá & Quản trị qua MockMvc trên MySQL thật (TC-CTL-01 -> TC-CTL-12).
 */
@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "GALAXORA_DB_TEST_URL", matches = "jdbc:mysql:.*")
@DisplayName("CTL - Controller, Spring Security và API trên MySQL (MockMvc)")
class ControllerSecurityMvcAsmTest {

    private static final AtomicInteger SEQ = new AtomicInteger(9000);

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private BCryptPasswordEncoder bcrypt;

    @DynamicPropertySource
    static void cauHinhMySql(DynamicPropertyRegistry registry) {
        String url = System.getenv("GALAXORA_DB_TEST_URL");
        if (!url.matches("jdbc:mysql://127\\.0\\.0\\.1:[0-9]+/galaxora_test_[a-z0-9_]+")) {
            throw new IllegalArgumentException("Kiểm thử yêu cầu schema galaxora_test_* trên MySQL cục bộ");
        }
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> System.getenv("GALAXORA_DB_TEST_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("GALAXORA_DB_TEST_PASSWORD"));
    }

    private Phien taoTaiKhoan(String vaiTro) {
        int so = SEQ.incrementAndGet();
        String email = "ctl-asm-" + so + "@galaxora.test";
        String sdt = "+8498" + String.format("%07d", so);
        LocalDateTime luc = LocalDateTime.now(ZoneOffset.UTC).withNano(0);
        jdbc.update("""
            INSERT INTO nguoi_dung (ho_ten, email, email_chuan_hoa, so_dien_thoai, mat_khau_bam, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """, "Tài khoản " + so, email, email, sdt, bcrypt.encode("MatKhauTest@1"), luc, luc);
        Long id = jdbc.queryForObject("SELECT id FROM nguoi_dung WHERE email_chuan_hoa=?", Long.class, email);
        jdbc.update("INSERT INTO nguoi_dung_vai_tro (nguoi_dung_id, vai_tro_id) SELECT ?, id FROM vai_tro WHERE ma=?",
            id, vaiTro);
        LocalDateTime updatedAt = jdbc.queryForObject("SELECT updated_at FROM nguoi_dung WHERE id=?",
            LocalDateTime.class, id);
        return new Phien(id, updatedAt, List.of(vaiTro));
    }

    private static RequestPostProcessor as(Phien phien) {
        var quyen = phien.vaiTro().stream().map(v -> new SimpleGrantedAuthority("ROLE_" + v)).toList();
        return authentication(UsernamePasswordAuthenticationToken.authenticated(phien, null, quyen));
    }

    @Test
    @DisplayName("TC-CTL-01: Endpoint công khai /api/he-thong/suc-khoe và /api/tai-khoan/csrf trả 200")
    void endpointCongKhaiTra200() throws Exception {
        mvc.perform(get("/api/he-thong/suc-khoe"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.trangThai").value("HOAT_DONG"));

        mvc.perform(get("/api/tai-khoan/csrf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.tenHeader").isNotEmpty())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("TC-CTL-02: POST /api/tai-khoan/dang-ky thiếu CSRF token bị chặn 403")
    void dangKyThieuCsrfTra403() throws Exception {
        String body = json.writeValueAsString(Map.of(
            "hoTen", "Người Test",
            "email", "nocsrf@galaxora.test",
            "soDienThoai", "+84912345670",
            "matKhau", "MatKhau@123"
        ));
        mvc.perform(post("/api/tai-khoan/dang-ky")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-CTL-03: POST /api/tai-khoan/dang-ky với email hoặc SĐT không hợp lệ trả 400")
    void dangKyEmailHoacSdtSaiTra400() throws Exception {
        String saiEmail = json.writeValueAsString(Map.of(
            "hoTen", "Người Test", "email", "khong-phai-email", "soDienThoai", "+84912345671", "matKhau", "MatKhau@123"));
        mvc.perform(post("/api/tai-khoan/dang-ky").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(saiEmail))
            .andExpect(status().isBadRequest());

        String saiSdt = json.writeValueAsString(Map.of(
            "hoTen", "Người Test", "email", "saisdt@galaxora.test", "soDienThoai", "0912345671", "matKhau", "MatKhau@123"));
        mvc.perform(post("/api/tai-khoan/dang-ky").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(saiSdt))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-CTL-04: POST /api/tai-khoan/dang-ky với mật khẩu vượt 72 byte UTF-8 (25 chữ 'ế') trả 400")
    void dangKyMatKhauVuot72ByteTra400() throws Exception {
        String body = json.writeValueAsString(Map.of(
            "hoTen", "Người Test",
            "email", "utf8pass@galaxora.test",
            "soDienThoai", "+84912345672",
            "matKhau", "ế".repeat(25)
        ));
        mvc.perform(post("/api/tai-khoan/dang-ky").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-CTL-05: Truy cập các API yêu cầu xác thực khi chưa đăng nhập đều trả 401")
    void chuaDangNhapVaoApiBaoVeTra401() throws Exception {
        for (String path : List.of("/api/quan-tri/tong-quan", "/api/gio-hang", "/api/tai-khoan/phien", "/api/tai-khoan/dia-chi")) {
            mvc.perform(get(path)).andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("TC-CTL-06: Vai trò USER truy cập /api/quan-tri/** bị chặn 403 Forbidden")
    void userVaoQuanTriTra403() throws Exception {
        var user = taoTaiKhoan("USER");
        mvc.perform(get("/api/quan-tri/tong-quan").with(as(user)))
            .andExpect(status().isForbidden());

        mvc.perform(post("/api/quan-tri/cua-hang/danh-muc").with(as(user)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("ten", "Trái phép", "hienThi", true))))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-CTL-07: Vai trò chỉ có ADMIN truy cập API dành riêng cho USER (/api/gio-hang, /dia-chi) bị chặn 403")
    void adminVaoApiChiCuaUserTra403() throws Exception {
        var admin = taoTaiKhoan("ADMIN");
        mvc.perform(get("/api/gio-hang").with(as(admin))).andExpect(status().isForbidden());
        mvc.perform(get("/api/tai-khoan/dia-chi").with(as(admin))).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("TC-CTL-08: CRUD Sổ địa chỉ và cô lập sở hữu giữa USER A và USER B")
    void crudSoDiaChiVaCoLapNguoiDung() throws Exception {
        var userA = taoTaiKhoan("USER");
        var userB = taoTaiKhoan("USER");

        mvc.perform(post("/api/tai-khoan/dia-chi").with(as(userA)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                    "nguoiNhan", "Lương Hoàng Dũng",
                    "soDienThoai", "+84912345678",
                    "diaChi", "137 Nguyễn Thị Thập, Đà Nẵng"))))
            .andExpect(status().isNoContent());

        var dsResp = mvc.perform(get("/api/tai-khoan/dia-chi").with(as(userA)))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray();
        var ds = json.readTree(dsResp);
        assertThat(ds.size()).isEqualTo(1);
        long diaChiId = ds.get(0).get("id").asLong();

        // USER B không thể sửa hay xóa địa chỉ của USER A -> 404
        mvc.perform(put("/api/tai-khoan/dia-chi/" + diaChiId).with(as(userB)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                    "nguoiNhan", "Hack", "soDienThoai", "+84900000000", "diaChi", "Khác"))))
            .andExpect(status().isNotFound());
        mvc.perform(delete("/api/tai-khoan/dia-chi/" + diaChiId).with(as(userB)).with(csrf()))
            .andExpect(status().isNotFound());

        // USER A sửa rồi xóa thành công
        mvc.perform(put("/api/tai-khoan/dia-chi/" + diaChiId).with(as(userA)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                    "nguoiNhan", "Dũng Đã Sửa", "soDienThoai", "+84912345679", "diaChi", "Liên Chiểu, Đà Nẵng"))))
            .andExpect(status().isNoContent());
        mvc.perform(delete("/api/tai-khoan/dia-chi/" + diaChiId).with(as(userA)).with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("TC-CTL-09: Admin gọi tự khóa chính tài khoản của mình bị từ chối 409 Conflict")
    void adminTuKhoaChinhMinhTra409() throws Exception {
        var admin = taoTaiKhoan("ADMIN");
        mvc.perform(put("/api/quan-tri/tai-khoan/" + admin.nguoiDungId() + "/khoa")
                .with(as(admin)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("khoa", true))))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("TC-CTL-10: Tạo sự kiện khám phá với ketThuc <= batDau trả 400 Bad Request")
    void taoSuKienSaiThoiGianTra400() throws Exception {
        var admin = taoTaiKhoan("ADMIN");
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC).withNano(0);
        mvc.perform(post("/api/quan-tri/su-kien").with(as(admin)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of(
                    "ten", "Sự kiện lỗi biên",
                    "moTa", "Thời điểm kết thúc bằng bắt đầu",
                    "batDau", now.toString(),
                    "ketThuc", now.toString(),
                    "hienThi", true))))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC-CTL-11: Đánh giá sản phẩm với số sao ngoài [1..5] trả 400 và đơn chưa giao trả 409")
    void danhGiaSaiSoSaoHoacDonChuaGiao() throws Exception {
        var user = taoTaiKhoan("USER");
        mvc.perform(put("/api/danh-gia/999999/1").with(as(user)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("soSao", 6, "noiDung", "Quá số sao"))))
            .andExpect(status().isBadRequest());

        mvc.perform(put("/api/danh-gia/999999/1").with(as(user)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.writeValueAsString(Map.of("soSao", 5, "noiDung", "Đánh giá đơn không tồn tại"))))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("TC-CTL-12: Tìm kiếm sản phẩm với sapXep sai, kichThuoc > 100 hoặc trang âm trả 400")
    void timKiemSanPhamThamSoKhongHopLeTra400() throws Exception {
        mvc.perform(get("/api/cua-hang/san-pham").param("sapXep", "khong-hop-le"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/cua-hang/san-pham").param("kichThuoc", "101"))
            .andExpect(status().isBadRequest());
        mvc.perform(get("/api/cua-hang/san-pham").param("trang", "-1"))
            .andExpect(status().isBadRequest());
    }
}
