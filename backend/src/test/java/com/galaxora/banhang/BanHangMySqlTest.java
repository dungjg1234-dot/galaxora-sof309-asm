package com.galaxora.banhang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockHttpServletResponse;

@SpringBootTest
@AutoConfigureMockMvc
@EnabledIfEnvironmentVariable(named = "GALAXORA_DB_TEST_URL", matches = "jdbc:mysql:.*")
class BanHangMySqlTest {
    private static final String QUAN_TRI = "/api/quan-tri";
    private static final String CUA_HANG = "/api/cua-hang";
    private static final AtomicInteger SO_THU_TU = new AtomicInteger(700);
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;
    @Autowired JdbcTemplate jdbc;
    @Autowired BCryptPasswordEncoder bcrypt;

    @DynamicPropertySource
    static void cauHinhMySql(DynamicPropertyRegistry registry) {
        String url = System.getenv("GALAXORA_DB_TEST_URL");
        if (!url.matches("jdbc:mysql://127\\.0\\.0\\.1:[0-9]+/galaxora_test_[a-z0-9_]+")) {
            throw new IllegalArgumentException("Cần schema MySQL thử cục bộ.");
        }
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> System.getenv("GALAXORA_DB_TEST_USER"));
        registry.add("spring.datasource.password", () -> System.getenv("GALAXORA_DB_TEST_PASSWORD"));
    }

    @Test
    void quanTriCongBoNoiDungSuKienVaKhoaNguoiDung() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        var luc = LocalDateTime.now(java.time.ZoneOffset.UTC);
        assertThat(yeuCau(admin, HttpMethod.PUT, QUAN_TRI + "/noi-dung/kham-pha/sun",
            Map.of("ten","Mặt Trời", "moTa","Ngôi sao trung tâm của Hệ Mặt Trời.")).getStatus()).isEqualTo(204);
        assertThat(noiDung(yeuCau(null, HttpMethod.GET, "/api/kham-pha/noi-dung", null)).get(0).path("ma").asText())
            .isEqualTo("sun");
        assertThat(yeuCau(admin, HttpMethod.POST, QUAN_TRI + "/su-kien", Map.of(
            "ten","Khám phá Hệ Mặt Trời", "moTa","Sự kiện kiểm thử.",
            "batDau",luc.minusHours(1).toString(),"ketThuc",luc.plusHours(1).toString(),"hienThi",true))
            .getStatus()).isEqualTo(204);
        assertThat(noiDung(yeuCau(user, HttpMethod.GET, "/api/nhiem-vu/thong-bao", null)).toString())
            .contains("Khám phá Hệ Mặt Trời");
        String khoa = QUAN_TRI + "/tai-khoan/" + user.nguoiDungId() + "/khoa";
        assertThat(yeuCau(admin, HttpMethod.PUT, khoa, Map.of("khoa",true)).getStatus()).isEqualTo(204);
        assertThat(yeuCau(user, HttpMethod.GET, "/api/nhiem-vu/ho-so", null).getStatus()).isEqualTo(401);
        assertThat(yeuCau(admin, HttpMethod.PUT, khoa, Map.of("khoa",false)).getStatus()).isEqualTo(204);
        assertThat(jdbc.queryForList("SELECT thao_tac FROM nhat_ky_quan_tri WHERE admin_id=? AND duong_dan=? ORDER BY id",
            String.class, admin.nguoiDungId(), khoa)).containsExactly("Khóa tài khoản", "Mở khóa tài khoản");
    }

    @Test
    void loiGhiNhatKyKhoPhaiHoanTacVaYeuCauLapChiGhiMotLan() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        var sp = sanPham(admin);
        nhapKho(admin, sp.id(), 5);
        String url = QUAN_TRI + "/kho/ton/" + sp.id() + "/dieu-chinh";
        var dieuChinh = Map.of("khoaYeuCau", UUID.randomUUID().toString(), "lyDo", "Kiểm thử rollback", "thayDoi", 2);
        assertThat(yeuCau(user, HttpMethod.POST, url, dieuChinh).getStatus()).isEqualTo(403);

        jdbc.execute("""
            ALTER TABLE nhat_ky_quan_tri ADD CONSTRAINT thu_loi_nhat_ky_kho
                CHECK (admin_id <> %d OR thao_tac <> 'Điều chỉnh tồn kho')
            """.formatted(admin.nguoiDungId()));
        try {
            assertThatThrownBy(() -> yeuCau(admin, HttpMethod.POST, url, dieuChinh))
                .rootCause().isInstanceOfSatisfying(java.sql.SQLException.class,
                    loi -> assertThat(loi.getErrorCode()).isEqualTo(3819));
            kiemTraTon(sp.id(), 5, 0);
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM giao_dich_kho WHERE san_pham_id=? AND loai='DIEU_CHINH'",
                Integer.class, sp.id())).isZero();
        } finally {
            jdbc.execute("ALTER TABLE nhat_ky_quan_tri DROP CHECK thu_loi_nhat_ky_kho");
        }

        assertThat(yeuCau(admin, HttpMethod.POST, url, dieuChinh).getStatus()).isEqualTo(204);
        assertThat(yeuCau(admin, HttpMethod.POST, url, dieuChinh).getStatus()).isEqualTo(204);
        kiemTraTon(sp.id(), 7, 0);
        var nhatKy = noiDung(yeuCau(admin, HttpMethod.GET, QUAN_TRI + "/nhat-ky?adminId=" + admin.nguoiDungId(), null));
        assertThat(nhatKy.size()).isEqualTo(4);
        assertThat(nhatKy.get(0).path("thaoTac").asText()).isEqualTo("Điều chỉnh tồn kho");
        assertThat(nhatKy.get(0).path("duongDan").asText()).isEqualTo(url);
        assertThat(nhatKy.get(1).path("thaoTac").asText()).isEqualTo("Nhập kho");
        assertThat(nhatKy.get(2).path("thaoTac").asText()).isEqualTo("Tạo sản phẩm");
        assertThat(nhatKy.get(3).path("thaoTac").asText()).isEqualTo("Tạo danh mục");
    }

    @Test
    void huyTruocXacNhanVaTuChoiHuyGiuDungTonVaNhatKy() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        datPhi(admin);
        var sp = sanPham(admin);
        nhapKho(admin, sp.id(), 4);
        long donHuy = datDon(user, sp.id(), 1, null);
        assertThat(yeuCau(user, HttpMethod.POST, "/api/don-hang/" + donHuy + "/huy", null).getStatus()).isEqualTo(204);
        kiemTraTon(sp.id(), 4, 0);
        assertThat(noiDung(yeuCau(user, HttpMethod.GET, "/api/don-hang/" + donHuy, null))
            .path("don").path("trangThai").asText()).isEqualTo("DA_HUY");

        long don = datDon(user, sp.id(), 2, null);
        assertThat(chuyen(admin, don, "DA_XAC_NHAN", false)).isEqualTo(204);
        assertThat(yeuCau(user, HttpMethod.POST, "/api/don-hang/" + don + "/yeu-cau-huy",
            Map.of("lyDo", "Khách đổi ý")).getStatus()).isEqualTo(204);
        long yeuCauId = noiDung(yeuCau(user, HttpMethod.GET, "/api/don-hang/" + don, null))
            .path("yeuCauHuy").get(0).path("id").asLong();
        String url = QUAN_TRI + "/don-hang/" + don + "/yeu-cau-huy/" + yeuCauId;
        var tuChoi = Map.of("duyet", false, "lyDo", "Lý do kiểm thử");
        assertThat(yeuCau(admin, HttpMethod.POST, url, tuChoi).getStatus()).isEqualTo(204);
        assertThat(yeuCau(admin, HttpMethod.POST, url, tuChoi).getStatus()).isEqualTo(204);
        kiemTraTon(sp.id(), 4, 2);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM nhat_ky_quan_tri WHERE duong_dan=? AND thao_tac=?",
            Long.class, url, "YEU_CAU_HUY CHO_DUYET -> TU_CHOI")).isEqualTo(1);
        assertThat(chuyen(admin, don, "DANG_CHUAN_BI", false)).isEqualTo(204);
        assertThat(chuyen(admin, don, "DANG_GIAO", false)).isEqualTo(204);
    }

    @Test
    void soDiaChiGioiHanTheoChuSoHuu() throws Exception {
        var user = taiKhoan("USER");
        var khac = taiKhoan("USER");
        var admin = taiKhoan("ADMIN");
        String url = "/api/tai-khoan/dia-chi";
        var diaChi = Map.of("nguoiNhan", "Người nhận thử", "soDienThoai", "+84912345678", "diaChi", "Địa chỉ thử");
        assertThat(yeuCau(null, HttpMethod.GET, url, null).getStatus()).isEqualTo(401);
        assertThat(yeuCau(admin, HttpMethod.GET, url, null).getStatus()).isEqualTo(403);
        assertThat(yeuCau(user, HttpMethod.POST, url, diaChi).getStatus()).isEqualTo(204);
        long id = noiDung(yeuCau(user, HttpMethod.GET, url, null)).get(0).path("id").asLong();
        String chiTiet = url + "/" + id;
        assertThat(noiDung(yeuCau(khac, HttpMethod.GET, url, null)).size()).isZero();
        assertThat(yeuCau(khac, HttpMethod.PUT, chiTiet, diaChi).getStatus()).isEqualTo(404);
        assertThat(yeuCau(khac, HttpMethod.DELETE, chiTiet, null).getStatus()).isEqualTo(404);
        var sua = Map.of("nguoiNhan", "Người nhận mới", "soDienThoai", "+84912345678", "diaChi", "Địa chỉ mới");
        assertThat(yeuCau(user, HttpMethod.PUT, chiTiet, sua).getStatus()).isEqualTo(204);
        assertThat(noiDung(yeuCau(user, HttpMethod.GET, url, null)).get(0).path("diaChi").asText()).isEqualTo("Địa chỉ mới");
        assertThat(yeuCau(user, HttpMethod.DELETE, chiTiet, null).getStatus()).isEqualTo(204);
        assertThat(noiDung(yeuCau(user, HttpMethod.GET, url, null)).size()).isZero();
    }

    @Test
    void hanhTrinhKhoaChuongCapXpMotLanVaGiuQuyenVoucher() throws Exception {
        var user = taiKhoan("USER");
        var admin = taiKhoan("ADMIN");
        var khac = taiKhoan("USER");
        String goc = "/api/nhiem-vu";
        assertThat(yeuCau(null, HttpMethod.GET, goc + "/tien-do", null).getStatus()).isEqualTo(401);
        assertThat(yeuCau(admin, HttpMethod.GET, goc + "/tien-do", null).getStatus()).isEqualTo(403);
        assertThat(yeuCau(user, HttpMethod.POST, goc + "/kham-pha/sun/mo-bang", null).getStatus()).isEqualTo(404);
        var moi = khamPha(user, "pluto");
        assertThat(moi.path("hoSo").path("tongXp").asInt()).isEqualTo(10);
        assertThat(moi.path("cacChuong").get(2).path("mo").asBoolean()).isFalse();
        assertThat(khamPha(user, "pluto").path("hoSo").path("tongXp").asInt()).isEqualTo(10);
        for (var ma : List.of("moon", "jupiter", "saturn", "uranus", "neptune", "mercury", "venus", "earth", "mars")) {
            khamPha(user, ma);
        }
        var cho = noiDung(yeuCau(user, HttpMethod.GET, goc + "/tien-do", null));
        assertThat(cho.path("hoSo").path("tongXp").asInt()).isEqualTo(200);
        assertThat(cho.path("hoSo").path("phanThuong").size()).isEqualTo(2);
        assertThat(cho.path("cacChuong").get(2).path("hoanThanh").asBoolean()).isTrue();
        assertThat(cho.path("voucherDangCho").asBoolean()).isTrue();
        var luc = LocalDateTime.now(java.time.ZoneOffset.UTC);
        assertThat(yeuCau(user, HttpMethod.PUT, QUAN_TRI + "/voucher-hanh-trinh", Map.of()).getStatus()).isEqualTo(403);
        assertThat(yeuCau(admin, HttpMethod.PUT, QUAN_TRI + "/voucher-hanh-trinh", Map.of(
            "loai","SO_TIEN", "giaTri",2000, "batDau",luc.minusDays(1).toString(), "ketThuc",luc.plusDays(1).toString(),
            "luotToiDa",1,"kichHoat",true)).getStatus()).isEqualTo(204);
        var nhan = noiDung(yeuCau(user, HttpMethod.POST, goc + "/voucher", null));
        assertThat(nhan.path("voucherDangCho").asBoolean()).isFalse();
        assertThat(nhan.path("hoSo").path("phanThuong").size()).isEqualTo(3);
        assertThat(noiDung(yeuCau(user, HttpMethod.POST, goc + "/voucher", null))
            .path("hoSo").path("phanThuong").size()).isEqualTo(3);
        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM voucher WHERE chu_so_huu_id=?", Integer.class, user.nguoiDungId()))
            .isEqualTo(1);
        assertThat(noiDung(yeuCau(khac, HttpMethod.GET, goc + "/ho-so", null)).path("tongXp").asInt()).isZero();
        assertThat(yeuCau(admin, HttpMethod.GET, QUAN_TRI + "/tong-quan", null).getStatus()).isEqualTo(200);
        var nhatKy = noiDung(yeuCau(admin, HttpMethod.GET, QUAN_TRI + "/nhat-ky?adminId=" + admin.nguoiDungId(), null));
        assertThat(nhatKy.size()).isEqualTo(1);
        assertThat(nhatKy.get(0).path("thaoTac").asText()).isEqualTo("Đổi mẫu voucher hành trình");
        jdbc.update("DELETE FROM cau_hinh_voucher_hanh_trinh WHERE id=1");
    }

    @Test
    void nhiemVuAdminCapThuongMotLanVaGiuVoucherDangCho() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        String quanTri = QUAN_TRI + "/nhiem-vu";
        String goc = "/api/nhiem-vu";
        var moi = Map.of("ten", "Khám phá Sao Hỏa", "moTa", "Nhiệm vụ kiểm thử.",
            "loaiDieuKien", "THIEN_THE", "maDieuKien", "mars", "xp", 25,
            "tenHuyHieu", "Nhà thám hiểm", "coVoucher", true, "kichHoat", true);
        assertThat(yeuCau(user, HttpMethod.POST, quanTri, moi).getStatus()).isEqualTo(403);
        var tao = yeuCau(admin, HttpMethod.POST, quanTri, moi);
        assertThat(tao.getStatus()).isEqualTo(201);
        long id = json.readTree(tao.getContentAsByteArray()).path("id").asLong();
        try {
            assertThat(noiDung(yeuCau(user, HttpMethod.POST, goc + "/doi-chieu", null))
                .path("nhiemVu").get(0).path("hoanThanh").asBoolean()).isFalse();
            var hoanThanh = khamPha(user, "mars");
            assertThat(hoanThanh.path("hoSo").path("tongXp").asInt()).isEqualTo(35);
            assertThat(hoanThanh.path("hoSo").path("phanThuong").get(0).path("tenHuyHieu").asText())
                .isEqualTo("Nhà thám hiểm");
            assertThat(hoanThanh.path("nhiemVu").get(0).path("voucherDangCho").asBoolean()).isTrue();
            var luc = LocalDateTime.now(java.time.ZoneOffset.UTC);
            assertThat(yeuCau(admin, HttpMethod.PUT, quanTri + "/" + id + "/voucher", Map.of(
                "loai", "SO_TIEN", "giaTri", 2000, "batDau", luc.minusDays(1).toString(),
                "ketThuc", luc.plusDays(1).toString(), "luotToiDa", 1, "kichHoat", true)).getStatus()).isEqualTo(204);
            try (var nhom = Executors.newFixedThreadPool(2)) {
                var a = nhom.submit(() -> yeuCau(user, HttpMethod.POST, goc + "/doi-chieu", null).getStatus());
                var b = nhom.submit(() -> yeuCau(user, HttpMethod.POST, goc + "/doi-chieu", null).getStatus());
                assertThat(a.get(15, TimeUnit.SECONDS)).isEqualTo(200);
                assertThat(b.get(15, TimeUnit.SECONDS)).isEqualTo(200);
            }
            var daCap = noiDung(yeuCau(user, HttpMethod.GET, goc + "/tien-do", null));
            assertThat(daCap.path("hoSo").path("tongXp").asInt()).isEqualTo(35);
            assertThat(daCap.path("hoSo").path("phanThuong").size()).isEqualTo(2);
            assertThat(daCap.path("nhiemVu").get(0).path("voucherDangCho").asBoolean()).isFalse();
            assertThat(jdbc.queryForObject("SELECT luot_da_cap FROM cau_hinh_voucher_nhiem_vu WHERE id=?", Integer.class, id))
                .isEqualTo(1);
        } finally {
            jdbc.update("DELETE FROM cau_hinh_voucher_nhiem_vu WHERE id=?", id);
            jdbc.update("DELETE FROM nhiem_vu_hoan_thanh WHERE nhiem_vu_id=?", id);
            jdbc.update("DELETE FROM nhiem_vu WHERE id=?", id);
        }
    }

    @Test
    void hienThiDanhMucVaSanPhamTheoQuyen() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        var sp = sanPham(admin);
        var hienThi = yeuCau(null, HttpMethod.GET, CUA_HANG + "/san-pham?tuKhoa=" + sp.sku(), null);
        assertThat(noiDung(hienThi).path("tong").asInt()).isEqualTo(1);
        assertThat(yeuCau(user, HttpMethod.GET, QUAN_TRI + "/kho/ton", null).getStatus()).isEqualTo(403);
        assertThat(yeuCau(null, HttpMethod.GET, "/api/gio-hang", null).getStatus()).isEqualTo(401);
        assertThat(yeuCau(user, HttpMethod.PUT, CUA_HANG + "/yeu-thich/" + sp.id(), Map.of("yeuThich", true))
            .getStatus()).isEqualTo(204);
        assertThat(noiDung(yeuCau(user, HttpMethod.GET, CUA_HANG + "/yeu-thich", null)).size()).isEqualTo(1);
        assertThat(yeuCau(admin, HttpMethod.PUT, QUAN_TRI + "/cua-hang/danh-muc/" + sp.danhMucId(),
            Map.of("ten", sp.sku(), "hienThi", false)).getStatus()).isEqualTo(200);
        assertThat(yeuCau(null, HttpMethod.GET, CUA_HANG + "/san-pham/" + sp.id(), null).getStatus()).isEqualTo(404);
    }

    @Test
    void khongGiuQuaTonKhiHaiDonCungXacNhanVaDuyetHuy() throws Exception {
        var admin = taiKhoan("ADMIN");
        var mot = taiKhoan("USER");
        var hai = taiKhoan("USER");
        long sp = sanPham(admin).id();
        nhapKho(admin, sp, 2);
        datPhi(admin);
        long donMot = datDon(mot, sp, 2, null);
        long donHai = datDon(hai, sp, 2, null);
        kiemTraTon(sp, 2, 0);
        var sanSang = new CountDownLatch(1);
        try (var nhom = Executors.newFixedThreadPool(2)) {
            var kqMot = nhom.submit(() -> { sanSang.await(); return chuyen(admin, donMot, "DA_XAC_NHAN", false); });
            var kqHai = nhom.submit(() -> { sanSang.await(); return chuyen(admin, donHai, "DA_XAC_NHAN", false); });
            sanSang.countDown();
            int motTrangThai = kqMot.get(15, TimeUnit.SECONDS);
            int haiTrangThai = kqHai.get(15, TimeUnit.SECONDS);
            assertThat(List.of(motTrangThai, haiTrangThai)).containsExactlyInAnyOrder(204, 409);
            kiemTraTon(sp, 2, 2);
            long don = motTrangThai == 204 ? donMot : donHai;
            var user = motTrangThai == 204 ? mot : hai;
            var nguoiKhac = motTrangThai == 204 ? hai : mot;
            assertThat(yeuCau(nguoiKhac, HttpMethod.GET, "/api/don-hang/" + don, null).getStatus()).isEqualTo(404);
            assertThat(yeuCau(user, HttpMethod.POST, "/api/don-hang/" + don + "/yeu-cau-huy", Map.of("lyDo", "Đổi ý"))
                .getStatus()).isEqualTo(204);
            assertThat(yeuCau(user, HttpMethod.POST, "/api/don-hang/" + don + "/yeu-cau-huy", Map.of("lyDo", "Lặp"))
                .getStatus()).isEqualTo(409);
            assertThat(chuyen(admin, don, "DANG_CHUAN_BI", false)).isEqualTo(204);
            assertThat(chuyen(admin, don, "DANG_GIAO", false)).isEqualTo(409);
            long yc = noiDung(yeuCau(user, HttpMethod.GET, "/api/don-hang/" + don, null))
                .path("yeuCauHuy").get(0).path("id").asLong();
            String duongDan = QUAN_TRI + "/don-hang/" + don + "/yeu-cau-huy/" + yc;
            for (int i = 0; i < 2; i++) {
                assertThat(yeuCau(admin, HttpMethod.POST, duongDan, Map.of("duyet", true, "lyDo", "Khách yêu cầu"))
                    .getStatus()).isEqualTo(204);
            }
            kiemTraTon(sp, 2, 0);
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM giao_dich_kho WHERE don_hang_id=? AND loai='GIAI_PHONG'",
                Long.class, don)).isEqualTo(1L);
        }
    }

    @Test
    void chotGiaVaVoucherGiaoCodVaLuuLichSuDanhGia() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        var sp = sanPham(admin);
        nhapKho(admin, sp.id(), 4);
        datPhi(admin);
        assertThat(yeuCau(admin, HttpMethod.POST, QUAN_TRI + "/voucher", Map.of(
            "ma", sp.sku(), "loai", "PHAN_TRAM", "giaTri", 10, "luotToiDa", 1, "kichHoat", true,
            "batDau", LocalDateTime.now().minusDays(1).toString(), "ketThuc", LocalDateTime.now().plusDays(1).toString()))
            .getStatus()).isEqualTo(204);
        long don = datDon(user, sp.id(), 2, sp.sku());
        String duongDan = "/api/don-hang/" + don;
        var snapshot = noiDung(yeuCau(user, HttpMethod.GET, duongDan, null)).path("don");
        assertThat(snapshot.path("tongTien").asInt()).isEqualTo(200000);
        assertThat(snapshot.path("giamGia").asInt()).isEqualTo(20000);
        assertThat(snapshot.path("daThuTien").asBoolean()).isFalse();
        String danhGia = "/api/danh-gia/" + don + "/" + sp.id();
        assertThat(yeuCau(user, HttpMethod.PUT, danhGia, Map.of("soSao", 4, "noiDung", "Hàng tốt")).getStatus()).isEqualTo(409);
        assertThat(chuyen(admin, don, "DA_XAC_NHAN", false)).isEqualTo(204);
        kiemTraTon(sp.id(), 4, 2);
        assertThat(chuyen(admin, don, "DANG_CHUAN_BI", false)).isEqualTo(204);
        assertThat(chuyen(admin, don, "DANG_GIAO", false)).isEqualTo(204);
        assertThat(yeuCau(user, HttpMethod.POST, duongDan + "/yeu-cau-huy", Map.of("lyDo", "Muộn"))
            .getStatus()).isEqualTo(409);
        assertThat(chuyen(admin, don, "DA_GIAO_THANH_CONG", false)).isEqualTo(400);
        assertThat(chuyen(admin, don, "DA_GIAO_THANH_CONG", true)).isEqualTo(204);
        kiemTraTon(sp.id(), 2, 0);
        assertThat(yeuCau(user, HttpMethod.PUT, danhGia, Map.of("soSao", 4, "noiDung", "Hàng tốt")).getStatus()).isEqualTo(204);
        assertThat(yeuCau(admin, HttpMethod.PUT, QUAN_TRI + "/cua-hang/danh-gia/" + don + "/" + sp.id() + "/hien-thi",
            Map.of("hienThi", false)).getStatus()).isEqualTo(204);
        assertThat(yeuCau(user, HttpMethod.PUT, danhGia, Map.of("soSao", 5, "noiDung", "Đánh giá cập nhật"))
            .getStatus()).isEqualTo(204);
        assertThat(jdbc.queryForObject("SELECT so_sao FROM lich_su_danh_gia WHERE don_hang_id=?", Integer.class, don))
            .isEqualTo(4);
        assertThat(noiDung(yeuCau(null, HttpMethod.GET, CUA_HANG + "/san-pham/" + sp.id() + "/danh-gia", null)).size())
            .isZero();
        long donLoi = datDon(user, sp.id(), 1, null);
        assertThat(chuyen(admin, donLoi, "DA_XAC_NHAN", false)).isEqualTo(204);
        assertThat(chuyen(admin, donLoi, "DANG_CHUAN_BI", false)).isEqualTo(204);
        assertThat(chuyen(admin, donLoi, "DANG_GIAO", false)).isEqualTo(204);
        assertThat(chuyen(admin, donLoi, "GIAO_THAT_BAI", false)).isEqualTo(204);
        kiemTraTon(sp.id(), 2, 0);
    }

    private record SanPham(long id, long danhMucId, String sku) {}

    @Test
    void xoaDanhMucVaSanPhamPhaiGiuLichSuKho() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        var sp = sanPham(admin);
        nhapKho(admin, sp.id(), 7);
        String sanPham = QUAN_TRI + "/cua-hang/san-pham/" + sp.id();
        String danhMuc = QUAN_TRI + "/cua-hang/danh-muc/" + sp.danhMucId();
        assertThat(yeuCau(user, HttpMethod.DELETE, sanPham, null).getStatus()).isEqualTo(403);
        assertThat(yeuCau(admin, HttpMethod.DELETE, danhMuc, null).getStatus()).isEqualTo(409);
        var ct = noiDung(yeuCau(null, HttpMethod.GET, CUA_HANG + "/san-pham/" + sp.id(), null));
        assertThat(ct.path("sanPham").path("soLuongKhaDung").asInt()).isEqualTo(7);
        assertThat(yeuCau(admin, HttpMethod.DELETE, sanPham, null).getStatus()).isEqualTo(204);
        assertThat(yeuCau(null, HttpMethod.GET, CUA_HANG + "/san-pham/" + sp.id(), null).getStatus()).isEqualTo(404);
        assertThat(yeuCau(user, HttpMethod.POST, "/api/gio-hang/" + sp.id(), Map.of("soLuong", 1)).getStatus()).isEqualTo(404);
        assertThat(yeuCau(admin, HttpMethod.DELETE, danhMuc, null).getStatus()).isEqualTo(204);
        assertThat(noiDung(yeuCau(admin, HttpMethod.GET,
            QUAN_TRI + "/kho/ton/" + sp.id() + "/lich-su", null)).size()).isEqualTo(1);
    }

    @Test
    void anhNoiBoHienThiTrongChiTietVaGioHang() throws Exception {
        var admin = taiKhoan("ADMIN");
        var user = taiKhoan("USER");
        var sp = sanPham(admin);
        String url = "/cua-hang/san-pham/" + sp.id();
        String anhBia = "/anh-san-pham/mo-hinh/tuong-decor-phi-hanh-gia-chibi-ngoi-mat-trang.webp";
        var duLieu = new java.util.HashMap<String, Object>(Map.of(
            "danhMucId", sp.danhMucId(), "sku", sp.sku(), "ten", sp.sku(),
            "moTaNgan", "", "moTa", "", "gia", 100000, "hienThi", true, "kinhDoanh", true));
        duLieu.put("hinhAnh", List.of(Map.of("duongDan", anhBia, "moTa", "Ảnh bìa")));
        assertThat(id(yeuCau(admin, HttpMethod.PUT, QUAN_TRI + url, duLieu))).isEqualTo(sp.id());
        duLieu.put("hinhAnh", List.of(Map.of("duongDan", "/anh-san-pham/../tep.webp", "moTa", "")));
        assertThat(yeuCau(admin, HttpMethod.PUT, QUAN_TRI + url, duLieu).getStatus()).isEqualTo(400);
        assertThat(noiDung(yeuCau(null, HttpMethod.GET, "/api" + url, null)).path("sanPham").path("anh").asText())
            .isEqualTo(anhBia);

        nhapKho(admin, sp.id(), 3);
        assertThat(yeuCau(user, HttpMethod.POST, "/api/gio-hang/" + sp.id(), Map.of("soLuong", 2)).getStatus())
            .isEqualTo(204);
        var gio = noiDung(yeuCau(user, HttpMethod.GET, "/api/gio-hang", null));
        assertThat(gio.path("chiTiet").get(0).path("anh").asText()).isEqualTo(anhBia);
        assertThat(gio.path("tamTinh").decimalValue()).isEqualByComparingTo("200000");
    }

    private SanPham sanPham(Phien admin) throws Exception {
        String ma = "THU-" + UUID.randomUUID();
        long dm = id(yeuCau(admin, HttpMethod.POST, QUAN_TRI + "/cua-hang/danh-muc", Map.of("ten", ma, "hienThi", true)));
        long sp = id(yeuCau(admin, HttpMethod.POST, QUAN_TRI + "/cua-hang/san-pham", Map.of(
            "danhMucId", dm, "sku", ma, "ten", ma, "moTaNgan", "", "moTa", "", "gia", 100000,
            "hienThi", true, "kinhDoanh", true, "hinhAnh", List.of())));
        return new SanPham(sp, dm, ma);
    }

    private void nhapKho(Phien admin, long sp, int sl) throws Exception {
        var data = Map.of("khoaYeuCau", UUID.randomUUID().toString(), "lyDo", "Kiểm thử",
            "chiTiet", List.of(Map.of("sanPhamId", sp, "soLuong", sl)));
        long mot = id(yeuCau(admin, HttpMethod.POST, QUAN_TRI + "/kho/phieu-nhap", data));
        assertThat(id(yeuCau(admin, HttpMethod.POST, QUAN_TRI + "/kho/phieu-nhap", data))).isEqualTo(mot);
        kiemTraTon(sp, sl, 0);
    }

    private void datPhi(Phien admin) throws Exception {
        assertThat(yeuCau(admin, HttpMethod.PUT, QUAN_TRI + "/don-hang/phi-van-chuyen", Map.of("giaTri", 20000))
            .getStatus()).isEqualTo(204);
    }

    private long datDon(Phien user, long sp, int sl, String voucher) throws Exception {
        assertThat(yeuCau(user, HttpMethod.PUT, "/api/gio-hang/" + sp, Map.of("soLuong", sl)).getStatus()).isEqualTo(204);
        var baoGia = noiDung(yeuCau(user, HttpMethod.GET,
            "/api/gio-hang/tinh-tien" + (voucher == null ? "" : "?maVoucher=" + voucher), null));
        var duLieu = Map.of("khoaYeuCau", UUID.randomUUID().toString(), "nguoiNhan", "Kiểm thử",
            "soDienThoai", "+12025550108", "diaChi", "Địa chỉ kiểm thử", "maVoucher", voucher == null ? "" : voucher,
            "tongTienDaXacNhan", baoGia.path("tongTien").decimalValue());
        long don = id(yeuCau(user, HttpMethod.POST, "/api/don-hang", duLieu));
        assertThat(id(yeuCau(user, HttpMethod.POST, "/api/don-hang", duLieu))).isEqualTo(don);
        return don;
    }

    private int chuyen(Phien admin, long don, String trangThai, boolean thuTien) throws Exception {
        return yeuCau(admin, HttpMethod.POST, QUAN_TRI + "/don-hang/" + don + "/trang-thai",
            Map.of("trangThai", trangThai, "daThuTien", thuTien, "lyDo", "Kiểm thử")).getStatus();
    }

    private void kiemTraTon(long sp, int thucTe, int dangGiu) {
        var ton = jdbc.queryForMap("SELECT so_luong_thuc_te,so_luong_dang_giu FROM ton_kho WHERE san_pham_id=?", sp);
        assertThat(ton.get("so_luong_thuc_te")).isEqualTo(thucTe);
        assertThat(ton.get("so_luong_dang_giu")).isEqualTo(dangGiu);
    }

    private Phien taiKhoan(String vaiTro) {
        int so = SO_THU_TU.incrementAndGet();
        String email = "banhang-" + so + "@example.test";
        jdbc.update("""
            INSERT INTO nguoi_dung (ho_ten,email,email_chuan_hoa,so_dien_thoai,mat_khau_bam,xac_minh_email_luc,created_at,updated_at)
            VALUES (?,?,?,?,?,UTC_TIMESTAMP(6),UTC_TIMESTAMP(6),UTC_TIMESTAMP(6))
            """, "Người kiểm thử", email, email, "+84999999" + so, bcrypt.encode("mat-khau-kiem-thu"));
        var phien = jdbc.queryForObject("SELECT id,updated_at FROM nguoi_dung WHERE email_chuan_hoa=?",
            (r, i) -> new Phien(r.getLong(1), r.getObject(2, LocalDateTime.class), List.of(vaiTro)), email);
        jdbc.update("""
            INSERT INTO nguoi_dung_vai_tro (nguoi_dung_id,vai_tro_id) SELECT ?,id FROM vai_tro WHERE ma=?
            """, phien.nguoiDungId(), vaiTro);
        return phien;
    }

    private MockHttpServletResponse yeuCau(Phien phien, HttpMethod phuongThuc, String url, Object noiDung) throws Exception {
        return yeuCau(phien, phuongThuc, url, noiDung, null);
    }

    private JsonNode khamPha(Phien phien, String ma) throws Exception {
        var session = new org.springframework.mock.web.MockHttpSession();
        String url = "/api/nhiem-vu/kham-pha/" + ma;
        var ve = noiDung(yeuCau(phien, HttpMethod.POST, url + "/mo-bang", null, session));
        Thread.sleep(ve.path("choMiliGiay").asInt());
        return noiDung(yeuCau(phien, HttpMethod.POST, url, Map.of("ve", ve.path("ve").asText()), session));
    }

    private MockHttpServletResponse yeuCau(Phien phien, HttpMethod phuongThuc, String url, Object noiDung,
        org.springframework.mock.web.MockHttpSession session) throws Exception {
        var yeuCau = request(phuongThuc, url).with(csrf());
        if (session != null) yeuCau.session(session);
        if (phien != null) {
            yeuCau.with(authentication(UsernamePasswordAuthenticationToken.authenticated(phien, null,
                phien.vaiTro().stream().map(v -> new SimpleGrantedAuthority("ROLE_" + v)).toList())));
        }
        if (noiDung != null) yeuCau.contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsBytes(noiDung));
        return mvc.perform(yeuCau).andReturn().getResponse();
    }

    private JsonNode noiDung(MockHttpServletResponse r) throws Exception {
        assertThat(r.getStatus()).as(r.getContentAsString()).isEqualTo(200);
        return json.readTree(r.getContentAsByteArray());
    }

    private long id(MockHttpServletResponse r) throws Exception {
        return noiDung(r).path("id").asLong();
    }
}
