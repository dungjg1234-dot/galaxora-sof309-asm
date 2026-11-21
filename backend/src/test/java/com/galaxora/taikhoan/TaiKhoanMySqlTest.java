package com.galaxora.taikhoan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;
import java.util.Map;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "GALAXORA_DB_TEST_URL", matches = "jdbc:mysql:.*")
class TaiKhoanMySqlTest {
    private static final String API = "/api/tai-khoan";
    private static final String MAT_KHAU = "mat-khau-kiem-thu";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;
    @Autowired private JdbcClient jdbc;
    @Autowired private Flyway flyway;
    @Autowired private GioiHanDangNhap gioiHanDangNhap;

    @DynamicPropertySource
    static void cauHinhMySql(DynamicPropertyRegistry thuocTinh) {
        String url = System.getenv("GALAXORA_DB_TEST_URL");
        if (!url.matches("jdbc:mysql://127\\.0\\.0\\.1:[0-9]+/galaxora_test_[a-z0-9_]+")) {
            throw new IllegalArgumentException("Kiểm thử yêu cầu schema galaxora_test_* trên MySQL cục bộ");
        }
        thuocTinh.add("spring.datasource.url", () -> url);
        thuocTinh.add("spring.datasource.username", () -> System.getenv("GALAXORA_DB_TEST_USER"));
        thuocTinh.add("spring.datasource.password", () -> System.getenv("GALAXORA_DB_TEST_PASSWORD"));
    }

    @Test
    void migrationChayLaiVaCacRangBuocDuLieu() {
        assertThat(flyway.migrate().migrationsExecuted).isZero();
        assertThat(jdbc.sql("SELECT ma FROM vai_tro ORDER BY ma").query(String.class).list())
            .containsExactly("ADMIN", "USER");
        assertThatThrownBy(() -> jdbc.sql("INSERT INTO vai_tro (ma) VALUES ('USER')").update())
            .isInstanceOf(DataIntegrityViolationException.class);
        assertThatThrownBy(() -> jdbc.sql("INSERT INTO vai_tro (ma) VALUES ('KHACH')").update())
            .isInstanceOfSatisfying(UncategorizedSQLException.class,
                loi -> assertThat(loi.getSQLException().getErrorCode()).isEqualTo(3819));
        assertThatThrownBy(() -> jdbc.sql("""
            INSERT INTO nguoi_dung_vai_tro (nguoi_dung_id, vai_tro_id)
            SELECT -1, id FROM vai_tro WHERE ma = 'USER'
            """).update()).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void doiVaiTroLamMatHieuLucPhienHienCo() throws Exception {
        String email = "doi-vai-tro@example.test";
        var phien = phienMoi();
        assertThat(taoTaiKhoan(phien, email, "+84912345127")).isEqualTo(202);
        assertThat(dangNhap(phien, email, MAT_KHAU, "192.0.2.27")).isEqualTo(200);
        assertThat(mvc.perform(get(API + "/phien").session(phien)).andReturn().getResponse().getStatus()).isEqualTo(200);
        long id = jdbc.sql("SELECT id FROM nguoi_dung WHERE email_chuan_hoa=?").param(email).query(Long.class).single();
        jdbc.sql("INSERT INTO nguoi_dung_vai_tro SELECT ?,id FROM vai_tro WHERE ma='ADMIN'").param(id).update();
        var phienAdmin = phienMoi();
        assertThat(dangNhap(phienAdmin, email, MAT_KHAU, "192.0.2.27")).isEqualTo(200);
        assertThat(mvc.perform(get("/api/quan-tri/tong-quan").session(phienAdmin)).andReturn().getResponse().getStatus())
            .isEqualTo(200);
        jdbc.sql("DELETE FROM nguoi_dung_vai_tro WHERE nguoi_dung_id=? AND vai_tro_id=(SELECT id FROM vai_tro WHERE ma='ADMIN')")
            .param(id).update();
        assertThat(mvc.perform(get("/api/quan-tri/tong-quan").session(phienAdmin)).andReturn().getResponse().getStatus())
            .isEqualTo(401);
        assertThat(phienAdmin.isInvalid()).isTrue();
    }

    @Test
    void doiMatKhauCanMatKhauHienTaiVaVoHieuHoaPhienCu() throws Exception {
        String email = "doi-mat-khau@example.test";
        String dienThoai = "+84912111609";
        String matKhauMoi = "mat-khau-moi-kiem-thu";
        var phien = phienMoi();
        var phienKhac = phienMoi();
        assertThat(taoTaiKhoan(phien, email, dienThoai)).isEqualTo(202);
        assertThat(dangNhap(phien, email, MAT_KHAU, "192.0.2.20")).isEqualTo(200);
        assertThat(dangNhap(phienKhac, dienThoai, MAT_KHAU, "192.0.2.21")).isEqualTo(200);
        assertThat(gui(phien, "/doi-mat-khau", Map.of("matKhau", "khong-dung", "matKhauMoi", matKhauMoi)))
            .isEqualTo(400);
        assertThat(mvc.perform(get(API + "/phien").session(phien)).andReturn().getResponse().getStatus())
            .isEqualTo(200);

        assertThat(gui(phien, "/doi-mat-khau", Map.of("matKhau", MAT_KHAU, "matKhauMoi", matKhauMoi)))
            .isEqualTo(204);
        assertThat(phien.isInvalid()).isTrue();
        assertThat(mvc.perform(get(API + "/phien").session(phienKhac)).andReturn().getResponse().getStatus())
            .isEqualTo(401);
        assertThat(dangNhap(phienMoi(), dienThoai, MAT_KHAU, "192.0.2.20")).isEqualTo(401);
        assertThat(dangNhap(phienMoi(), dienThoai, matKhauMoi, "192.0.2.20")).isEqualTo(200);
    }

    @Test
    void dangNhapNgaySauDangKyBangEmailHoacSoDienThoai() throws Exception {
        String email = "VongDoi@Example.test";
        String dienThoai = "+84 (912) 345-601";
        var phien = phienMoi();
        assertThat(taoTaiKhoan(phien, email, dienThoai)).isEqualTo(202);
        assertThat(taoTaiKhoan(phien, "vongdoi@example.test", "+84912345699")).isEqualTo(202);
        assertThat(taoTaiKhoan(phien, "trung-dien-thoai@example.test", "+84912345601")).isEqualTo(202);
        assertThat(jdbc.sql("SELECT COUNT(*) FROM nguoi_dung WHERE email_chuan_hoa = 'vongdoi@example.test' OR so_dien_thoai = '+84912345601'")
            .query(Integer.class).single()).isEqualTo(1);
        String idCu = phien.getId();
        assertThat(dangNhap(phien, "VONGDOI@example.test", MAT_KHAU, "192.0.2.1")).isEqualTo(200);
        assertThat(phien.getId()).isNotEqualTo(idCu);
        var phanHoiPhien = mvc.perform(get(API + "/phien").session(phien)).andReturn().getResponse();
        assertThat(phanHoiPhien.getStatus()).isEqualTo(200);
        assertThat(json.readTree(phanHoiPhien.getContentAsByteArray()).get("vaiTro").get(0).asText()).isEqualTo("USER");

        var phienDienThoai = phienMoi();
        assertThat(dangNhap(phienDienThoai, dienThoai, MAT_KHAU, "192.0.2.1")).isEqualTo(200);
        assertThat(gui(phienDienThoai, "/dang-xuat", Map.of())).isEqualTo(204);
        assertThat(phienDienThoai.isInvalid()).isTrue();
        assertThat(mvc.perform(get(API + "/phien")).andReturn().getResponse().getStatus()).isEqualTo(401);
    }

    @Test
    void daGoCacApiGuiThuVaXacMinh() throws Exception {
        String email = "go-api-gui-thu@example.test";
        var phien = phienMoi();
        assertThat(taoTaiKhoan(phien, email, "+84912345602")).isEqualTo(202);
        assertThat(dangNhap(phien, email, MAT_KHAU, "192.0.2.22")).isEqualTo(200);
        for (String duongDan : List.of("/gui-lai-ma", "/xac-minh-email", "/quen-mat-khau",
                "/dat-lai-mat-khau", "/doi-lien-he/email", "/doi-lien-he/dien-thoai", "/doi-lien-he/xac-minh")) {
            assertThat(gui(phien, duongDan, Map.of())).as(duongDan).isEqualTo(404);
        }
    }

    @Test
    void chanCsrfVaGioiHanDangNhapTheoTaiKhoanVaIp() throws Exception {
        String email = "gioihan@example.test";
        String dienThoai = "+84912345603";
        var phien = phienMoi();
        assertThat(mvc.perform(post(API + "/dang-ky").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andReturn().getResponse().getStatus()).isEqualTo(403);
        assertThat(taoTaiKhoan(phien, email, dienThoai)).isEqualTo(202);
        for (int i = 0; i < 5; i++) {
            assertThat(dangNhap(phien, email, "sai", "192.0.2.2")).isEqualTo(401);
        }
        assertThat(dangNhap(phien, dienThoai, MAT_KHAU, "192.0.2.3")).isEqualTo(401);
        assertThat(gioiHanDangNhap.canhBaoGanDay().theoTaiKhoan()).isTrue();
        for (int i = 5; i < 30; i++) {
            assertThat(dangNhap(phien, email, "sai", "192.0.2.2")).isEqualTo(401);
        }
        assertThat(dangNhap(phien, email, "sai", "192.0.2.2")).isEqualTo(429);
        assertThat(gioiHanDangNhap.canhBaoGanDay().theoIp()).isTrue();
    }

    private MockHttpSession phienMoi() {
        return new MockHttpSession();
    }

    private int taoTaiKhoan(MockHttpSession phien, String email, String dienThoai) throws Exception {
        return gui(phien, "/dang-ky", Map.of("hoTen", "Tài khoản kiểm thử", "email", email,
            "soDienThoai", dienThoai, "matKhau", MAT_KHAU, "vaiTro", "ADMIN"));
    }

    private int dangNhap(MockHttpSession phien, String dinhDanh, String matKhau, String ip) throws Exception {
        return gui(phien, "/dang-nhap", Map.of("dinhDanh", dinhDanh, "matKhau", matKhau), ip);
    }

    private int gui(MockHttpSession phien, String duongDan, Map<String, String> duLieu) throws Exception {
        return gui(phien, duongDan, duLieu, "192.0.2.10");
    }

    private int gui(MockHttpSession phien, String duongDan, Map<String, String> duLieu, String ip) throws Exception {
        var csrf = mvc.perform(get(API + "/csrf").session(phien)).andReturn().getResponse();
        assertThat(csrf.getStatus()).isEqualTo(200);
        var token = json.readTree(csrf.getContentAsByteArray());
        return mvc.perform(post(API + duongDan).session(phien)
            .with(yeuCau -> { yeuCau.setRemoteAddr(ip); return yeuCau; })
            .header(token.get("tenHeader").asText(), token.get("token").asText())
            .contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(duLieu)))
            .andReturn().getResponse().getStatus();
    }
}
