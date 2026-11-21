package com.galaxora.cuahang;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Y3 - Module GLB: Kiểm thử kiểm định tệp mô hình 3D GLB/glTF (TC-GLB-01 -> TC-GLB-09).
 */
@DisplayName("GLB - Kiểm duyệt và tải lên mô hình 3D (KhoMoHinhSanPham)")
class KhoMoHinhSanPhamAsmTest {

    private static final String NGUON_HOP_LE = "https://sketchfab.com/models/galaxora-demo";
    private JdbcTemplate jdbc;
    private KhoMoHinhSanPham khoMoHinh;

    @BeforeEach
    void thietLap() {
        jdbc = mock(JdbcTemplate.class);
        khoMoHinh = new KhoMoHinhSanPham(jdbc, new ObjectMapper());
    }

    @Test
    @DisplayName("TC-GLB-01: Nguồn mô hình không phải HTTPS bị chặn (400)")
    void nguonKhongPhaiHttpsBiChan() {
        var tep = new MockMultipartFile("tep", "saturn.gltf", "model/gltf+json",
            "{\"asset\":{\"version\":\"2.0\"}}".getBytes(StandardCharsets.UTF_8));
        assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", "http://evil.test/m.gltf", "TacGia", "GhiCong"))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @ParameterizedTest(name = "TC-GLB-02: Tên tệp chứa ký tự đường dẫn \"{0}\" (path traversal) bị chặn")
    @ValueSource(strings = {"../../etc/passwd.glb", "sub\\model.glb", "a/b.gltf"})
    void tenTepChuaPathTraversalBiChan(String tenTep) {
        var tep = new MockMultipartFile("tep", tenTep, "application/octet-stream", new byte[]{1, 2, 3});
        assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "TacGia", "GhiCong"))
            .isInstanceOfSatisfying(ResponseStatusException.class,
                e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("TC-GLB-03: Biên dung lượng (0 byte, > 8 MiB) và tên tệp > 255 ký tự bị từ chối")
    void bienDungLuongVaDoDaiTenTep() {
        var tepRong = new MockMultipartFile("tep", "a.glb", "model/gltf-binary", new byte[0]);
        var tepQuaLon = new MockMultipartFile("tep", "a.glb", "model/gltf-binary", new byte[8 * 1024 * 1024 + 1]);
        var tenQuaDai = new MockMultipartFile("tep", "a".repeat(252) + ".glb", "model/gltf-binary", new byte[]{1});

        for (var tep : List.of(tepRong, tepQuaLon, tenQuaDai)) {
            assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "TacGia", "GhiCong"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                    e -> assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        }
    }

    @ParameterizedTest(name = "TC-GLB-04: Phần mở rộng không hợp lệ \"{0}\" bị từ chối")
    @ValueSource(strings = {"model.obj", "script.exe", "photo.png", "archive.zip"})
    void dinhDangKhongHoTroBiTuChoi(String tenTep) {
        var tep = new MockMultipartFile("tep", tenTep, "application/octet-stream", new byte[]{1, 2, 3});
        assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "TacGia", "GhiCong"))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("GLB hoặc glTF");
            });
    }

    @Test
    @DisplayName("TC-GLB-05: Tệp .glb sai magic header hoặc ngắn hơn 20 byte bị từ chối")
    void glbSaiHeaderBiTuChoi() {
        var tepNgan = new MockMultipartFile("tep", "bad.glb", "model/gltf-binary", new byte[10]);
        var saiMagic = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN)
            .putInt(0x11223344).putInt(2).putInt(24).putInt(4).putInt(0x4e4f534a).putInt(0).array();
        var tepSaiMagic = new MockMultipartFile("tep", "bad2.glb", "model/gltf-binary", saiMagic);

        for (var tep : List.of(tepNgan, tepSaiMagic)) {
            assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "TacGia", "GhiCong"))
                .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                    assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(e.getReason()).contains("Header GLB 2.0 không hợp lệ");
                });
        }
    }

    @Test
    @DisplayName("TC-GLB-06: Tệp .glb đúng magic nhưng thiếu chunk JSON (0x4e4f534a) bị từ chối")
    void glbThieuChunkJsonBiTuChoi() {
        byte[] duLieu = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN)
            .putInt(0x46546c67).putInt(2).putInt(24).putInt(4).putInt(0x004e4942).putInt(0).array();
        var tep = new MockMultipartFile("tep", "nojson.glb", "model/gltf-binary", duLieu);
        assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "TacGia", "GhiCong"))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("thiếu phần JSON hợp lệ");
            });
    }

    @Test
    @DisplayName("TC-GLB-07: Tệp .gltf phiên bản 1.0 bị từ chối (chỉ nhận glTF 2.0)")
    void gltfPhienBan1BiTuChoi() {
        var tep = new MockMultipartFile("tep", "old.gltf", "model/gltf+json",
            "{\"asset\":{\"version\":\"1.0\"}}".getBytes(StandardCharsets.UTF_8));
        assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "TacGia", "GhiCong"))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("glTF 2.0");
            });
    }

    @Test
    @DisplayName("TC-GLB-08: Tệp .gltf 2.0 tham chiếu URI ngoài (không phải data:) bị chặn")
    void gltfThamChieuUriNgoaiBiChan() {
        String noiDung = "{\"asset\":{\"version\":\"2.0\"},\"images\":[{\"uri\":\"https://evil.test/tex.png\"}]}";
        var tep = new MockMultipartFile("tep", "ext.gltf", "model/gltf+json", noiDung.getBytes(StandardCharsets.UTF_8));
        assertThatThrownBy(() -> khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "TacGia", "GhiCong"))
            .isInstanceOfSatisfying(ResponseStatusException.class, e -> {
                assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getReason()).contains("đóng gói tài nguyên bên trong");
            });
    }

    @Test
    @DisplayName("TC-GLB-09: Tệp .glb 2.0 tự chứa hợp lệ được lưu ở trạng thái chờ duyệt")
    @SuppressWarnings("unchecked")
    void glbHopLeDuocLuuChoDuyet() {
        byte[] jsonBytes = "{\"asset\":{\"version\":\"2.0\"},\"buffers\":[{\"uri\":\"data:application/octet-stream;base64,AA==\"}]}"
            .getBytes(StandardCharsets.UTF_8);
        int tong = 20 + jsonBytes.length;
        byte[] glb = ByteBuffer.allocate(tong).order(ByteOrder.LITTLE_ENDIAN)
            .putInt(0x46546c67).putInt(2).putInt(tong).putInt(jsonBytes.length).putInt(0x4e4f534a)
            .put(jsonBytes).array();
        var tep = new MockMultipartFile("tep", "figure.GLB", "model/gltf-binary", glb);

        var kyVong = new KhoMoHinhSanPham.ThongTin("figure.GLB", "glb", tong, "CC BY 4.0", NGUON_HOP_LE, "NASA", "NASA", false);
        when(jdbc.query(anyString(), any(RowMapper.class), eq(1L))).thenReturn(List.of(kyVong));

        var ketQua = khoMoHinh.taiLen(1L, tep, "CC BY 4.0", NGUON_HOP_LE, "NASA", "NASA");
        assertThat(ketQua.dinhDang()).isEqualTo("glb");
        assertThat(ketQua.daDuyet()).isFalse();
        verify(jdbc).update(anyString(), eq(1L), eq("figure.GLB"), eq("glb"), eq(tong),
            eq("CC BY 4.0"), eq(NGUON_HOP_LE), eq("NASA"), eq("NASA"), eq(glb));
    }
}
