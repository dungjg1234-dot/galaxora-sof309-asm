package com.galaxora.cuahang;

import static com.galaxora.banhang.LoiBanHang.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
class KhoMoHinhSanPham {
    private static final String CONG_KHAI = " AND m.da_duyet AND s.hien_thi AND d.hien_thi";
    private static final String BANG = """
        FROM mo_hinh_3d_san_pham m JOIN san_pham s ON s.id=m.san_pham_id
        JOIN danh_muc d ON d.id=s.danh_muc_id WHERE m.san_pham_id=?
        """;
    private final JdbcTemplate jdbc;
    private final ObjectMapper json;
    record ThongTin(String tenTep, String dinhDang, long dungLuong, String giayPhep,
                    String nguon, String tacGia, String ghiCong, boolean daDuyet) {}
    record Tep(String dinhDang, byte[] duLieu) {}

    KhoMoHinhSanPham(JdbcTemplate jdbc, ObjectMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

    @Transactional
    ThongTin taiLen(long id, MultipartFile tep, String giayPhep, String nguon, String tacGia, String ghiCong) {
        DichVuCuaHang.kiemTraUrl(nguon);
        String ten = tep.getOriginalFilename();
        if (ten == null || ten.length() > 255 || ten.contains("/") || ten.contains("\\")
            || tep.isEmpty() || tep.getSize() > 8 * 1024 * 1024) {
            throw khongHopLe("Tệp mô hình phải có tên hợp lệ và không quá 8 MiB.");
        }
        String tenThuong = ten.toLowerCase(Locale.ROOT);
        String dinhDang = tenThuong.endsWith(".glb") ? "glb" : tenThuong.endsWith(".gltf") ? "gltf" : "";
        if (dinhDang.isEmpty()) throw khongHopLe("Chỉ chấp nhận tệp GLB hoặc glTF.");
        byte[] noiDung;
        try {
            noiDung = tep.getBytes();
            kiemTraMoHinh(dinhDang, noiDung);
        } catch (IOException e) {
            throw khongHopLe("Không đọc được mô hình 3D.");
        }
        jdbc.update("""
            INSERT INTO mo_hinh_3d_san_pham
                (san_pham_id,ten_tep,dinh_dang,dung_luong,giay_phep,nguon,tac_gia,ghi_cong,noi_dung)
            VALUES (?,?,?,?,?,?,?,?,?) AS moi
            ON DUPLICATE KEY UPDATE ten_tep=moi.ten_tep,dinh_dang=moi.dinh_dang,dung_luong=moi.dung_luong,
                giay_phep=moi.giay_phep,nguon=moi.nguon,tac_gia=moi.tac_gia,ghi_cong=moi.ghi_cong,
                noi_dung=moi.noi_dung,da_duyet=FALSE
            """, id, ten, dinhDang, noiDung.length, giayPhep, nguon, tacGia, ghiCong, noiDung);
        return thongTin(id, true);
    }

    private void kiemTraMoHinh(String dinhDang, byte[] noiDung) throws IOException {
        JsonNode cay;
        if (dinhDang.equals("glb")) {
            var dem = ByteBuffer.wrap(noiDung).order(ByteOrder.LITTLE_ENDIAN);
            if (dem.remaining() < 20 || dem.getInt() != 0x46546c67 || dem.getInt() != 2
                || dem.getInt() != noiDung.length) {
                throw khongHopLe("Header GLB 2.0 không hợp lệ.");
            }
            int doDai = dem.getInt();
            if (dem.getInt() != 0x4e4f534a || doDai <= 0 || doDai > dem.remaining()) {
                throw khongHopLe("GLB thiếu phần JSON hợp lệ.");
            }
            cay = json.readTree(noiDung, 20, doDai);
        } else {
            cay = json.readTree(noiDung);
        }
        if (cay == null || !"2.0".equals(cay.path("asset").path("version").asText())) {
            throw khongHopLe("Tệp phải là glTF 2.0.");
        }
        for (String truong : new String[]{"buffers", "images"}) {
            for (var taiNguyen : cay.path(truong)) {
                if (taiNguyen.has("uri") && !taiNguyen.path("uri").asText().startsWith("data:")) {
                    throw khongHopLe("Tệp phải đóng gói tài nguyên bên trong.");
                }
            }
        }
    }

    @Transactional
    ThongTin duyet(long id, boolean daDuyet) {
        if (jdbc.update("UPDATE mo_hinh_3d_san_pham SET da_duyet=? WHERE san_pham_id=?", daDuyet, id) == 0) {
            throw khongTimThay();
        }
        return thongTin(id, true);
    }

    void xoa(long id) {
        if (jdbc.update("DELETE FROM mo_hinh_3d_san_pham WHERE san_pham_id=?", id) == 0) throw khongTimThay();
    }

    ThongTin thongTin(long id, boolean quanTri) {
        return jdbc.query("SELECT m.ten_tep,m.dinh_dang,m.dung_luong,m.giay_phep,m.nguon,m.tac_gia,m.ghi_cong,m.da_duyet "
            + BANG + (quanTri ? "" : CONG_KHAI), (r, i) -> new ThongTin(r.getString(1), r.getString(2),
                r.getLong(3), r.getString(4), r.getString(5), r.getString(6), r.getString(7), r.getBoolean(8)), id)
            .stream().findFirst().orElseThrow(() -> khongTimThay());
    }

    Tep noiDung(long id) {
        return jdbc.query("SELECT m.dinh_dang,m.noi_dung " + BANG + CONG_KHAI,
            (r, i) -> new Tep(r.getString(1), r.getBytes(2)), id).stream().findFirst().orElseThrow(() -> khongTimThay());
    }
}
