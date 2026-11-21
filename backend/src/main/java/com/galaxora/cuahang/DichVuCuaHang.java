package com.galaxora.cuahang;

import static com.galaxora.banhang.LoiBanHang.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class DichVuCuaHang {
    private static final Pattern DUONG_DAN_ANH_NOI_BO =
        Pattern.compile("/anh-san-pham/(mo-hinh|phu-kien|thoi-trang|trang-tri)/[a-z0-9]+(?:-[a-z0-9]+)*\\.webp");
    private static final String COT_SAN_PHAM = """
        SELECT s.id, s.danh_muc_id, d.ten AS danh_muc, s.sku, s.ten, s.mo_ta_ngan,
               s.mo_ta, s.gia, s.gia_so_sanh, s.hien_thi, s.kinh_doanh,
               (SELECT a.duong_dan FROM hinh_anh_san_pham a WHERE a.san_pham_id=s.id ORDER BY a.id LIMIT 1) AS anh,
               EXISTS(SELECT 1 FROM mo_hinh_3d_san_pham m WHERE m.san_pham_id=s.id AND m.da_duyet) AS co_mo_hinh,
               k.so_luong_thuc_te-k.so_luong_dang_giu AS so_luong_kha_dung,
               CASE WHEN k.so_luong_thuc_te=k.so_luong_dang_giu THEN 'HET_HANG'
                    WHEN k.so_luong_thuc_te-k.so_luong_dang_giu <= c.nguong_ton_kho THEN 'SAP_HET'
                    ELSE 'CON_HANG' END AS trang_thai_kho
        FROM san_pham s JOIN danh_muc d ON d.id=s.danh_muc_id
        JOIN ton_kho k ON k.san_pham_id=s.id CROSS JOIN cau_hinh_ban_hang c
        """;
    private static final String CHUA_XOA = "NOT s.da_xoa AND NOT d.da_xoa";
    private static final String CONG_KHAI = CHUA_XOA + " AND s.hien_thi AND d.hien_thi";
    private static final RowMapper<SanPham> DOC_SAN_PHAM = (r, i) -> new SanPham(
        r.getLong("id"), r.getLong("danh_muc_id"), r.getString("danh_muc"), r.getString("sku"),
        r.getString("ten"), r.getString("mo_ta_ngan"), r.getString("mo_ta"), r.getBigDecimal("gia"),
        r.getBigDecimal("gia_so_sanh"), r.getBoolean("hien_thi"), r.getBoolean("kinh_doanh"),
        r.getString("anh"), r.getBoolean("co_mo_hinh"), r.getString("trang_thai_kho"), r.getInt("so_luong_kha_dung"));

    private final JdbcTemplate jdbc;
    private final SimpleJdbcInsert themDanhMuc;
    private final SimpleJdbcInsert themSanPham;

    DichVuCuaHang(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        themDanhMuc = new SimpleJdbcInsert(jdbc).withTableName("danh_muc")
            .usingColumns("ten", "hien_thi").usingGeneratedKeyColumns("id");
        themSanPham = new SimpleJdbcInsert(jdbc).withTableName("san_pham")
            .usingColumns("danh_muc_id", "sku", "ten", "mo_ta_ngan", "mo_ta", "gia", "gia_so_sanh",
                "hien_thi", "kinh_doanh").usingGeneratedKeyColumns("id");
    }

    record SanPham(long id, long danhMucId, String danhMuc, String sku, String ten, String moTaNgan,
                   String moTa, BigDecimal gia, BigDecimal giaSoSanh, boolean hienThi, boolean kinhDoanh,
                   String anh, boolean coMoHinh, String trangThaiKho, int soLuongKhaDung) {}
    record DanhMuc(long id, String ten, boolean hienThi) {}
    record Anh(String duongDan, String moTa) {}
    record Trang(List<SanPham> noiDung, long tong, int trang, int kichThuoc) {}
    record ChiTiet(SanPham sanPham, List<Anh> hinhAnh, List<SanPham> lienQuan) {}

    List<DanhMuc> danhMuc(boolean quanTri) {
        return jdbc.query("SELECT id,ten,hien_thi FROM danh_muc WHERE NOT da_xoa AND (? OR hien_thi) ORDER BY ten,id",
            (r, i) -> new DanhMuc(r.getLong(1), r.getString(2), r.getBoolean(3)), quanTri);
    }

    long luuDanhMuc(Long id, String ten, boolean hienThi) {
        if (id == null) {
            return themDanhMuc.executeAndReturnKey(Map.of("ten", ten.strip(), "hien_thi", hienThi)).longValue();
        }
        if (jdbc.update("UPDATE danh_muc SET ten=?, hien_thi=? WHERE id=? AND NOT da_xoa", ten.strip(), hienThi, id) == 0) {
            throw khongTimThay();
        }
        return id;
    }

    void xoaDanhMuc(long id) {
        if (jdbc.queryForList("SELECT id FROM danh_muc WHERE id=? AND NOT da_xoa FOR UPDATE", Long.class, id).isEmpty()) {
            throw khongTimThay();
        }
        if (!jdbc.queryForList("SELECT id FROM san_pham WHERE danh_muc_id=? AND NOT da_xoa LIMIT 1 FOR UPDATE", Long.class, id).isEmpty()) {
            throw xungDot("Hãy chuyển hoặc xóa các sản phẩm trong danh mục này trước.");
        }
        if (jdbc.update("DELETE FROM danh_muc WHERE id=? AND NOT EXISTS (SELECT 1 FROM san_pham WHERE danh_muc_id=?)", id, id) == 0) {
            jdbc.update("UPDATE danh_muc SET da_xoa=TRUE,hien_thi=FALSE WHERE id=?", id);
        }
    }

    void xoaSanPham(long id) {
        if (jdbc.update("UPDATE san_pham SET da_xoa=TRUE,hien_thi=FALSE,kinh_doanh=FALSE WHERE id=? AND NOT da_xoa", id) == 0) {
            throw khongTimThay();
        }
    }

    @Transactional(readOnly = true)
    Trang tim(String tuKhoa, Long danhMucId, String sapXep, int trang, int kichThuoc, boolean quanTri) {
        String loc = """
            WHERE NOT s.da_xoa AND NOT d.da_xoa AND (? OR (s.hien_thi AND d.hien_thi))
              AND (? IS NULL OR s.danh_muc_id=?)
              AND (LOCATE(?,s.ten)>0 OR LOCATE(?,s.sku)>0 OR LOCATE(?,s.mo_ta_ngan)>0 OR ?='')
            """;
        String thuTu = switch (sapXep) {
            case "gia-tang" -> "s.gia ASC,s.id DESC";
            case "gia-giam" -> "s.gia DESC,s.id DESC";
            case "ten" -> "s.ten,s.id DESC";
            case "moi" -> "s.id DESC";
            default -> throw khongHopLe("Cách sắp xếp không hợp lệ.");
        };
        String tu = tuKhoa.strip();
        var noiDung = jdbc.query(COT_SAN_PHAM + loc + " ORDER BY " + thuTu + " LIMIT ? OFFSET ?",
            DOC_SAN_PHAM, quanTri, danhMucId, danhMucId, tu, tu, tu, tu, kichThuoc, (long) trang * kichThuoc);
        Long tong = jdbc.queryForObject("SELECT COUNT(*) FROM san_pham s JOIN danh_muc d ON d.id=s.danh_muc_id " + loc,
            Long.class, quanTri, danhMucId, danhMucId, tu, tu, tu, tu);
        return new Trang(noiDung, tong, trang, kichThuoc);
    }

    @Transactional(readOnly = true)
    ChiTiet chiTiet(long id, boolean quanTri) {
        var sanPham = laySanPham(id, quanTri);
        var anh = jdbc.query("SELECT duong_dan,mo_ta FROM hinh_anh_san_pham WHERE san_pham_id=? ORDER BY id",
            (r, i) -> new Anh(r.getString(1), r.getString(2)), id);
        var lienQuan = quanTri ? List.<SanPham>of() : jdbc.query(COT_SAN_PHAM + " WHERE " + CONG_KHAI
            + " AND s.danh_muc_id=? AND s.id<>? ORDER BY s.id DESC LIMIT 4", DOC_SAN_PHAM, sanPham.danhMucId(), id);
        return new ChiTiet(sanPham, anh, lienQuan);
    }

    SanPham laySanPham(long id, boolean quanTri) {
        return jdbc.query(COT_SAN_PHAM + " WHERE s.id=? AND " + CHUA_XOA + " AND (? OR (s.hien_thi AND d.hien_thi))",
            DOC_SAN_PHAM, id, quanTri).stream().findFirst().orElseThrow(() -> khongTimThay());
    }

    @Transactional
    long luuSanPham(Long id, CuaHangController.GhiSanPham duLieu) {
        if (jdbc.queryForList("SELECT id FROM danh_muc WHERE id=? AND NOT da_xoa FOR SHARE", Long.class, duLieu.danhMucId()).isEmpty()) {
            throw khongTimThay();
        }
        duLieu.hinhAnh().forEach(anh -> {
            if (!DUONG_DAN_ANH_NOI_BO.matcher(anh.duongDan()).matches()) kiemTraUrl(anh.duongDan());
        });
        var thamSo = new java.util.HashMap<String, Object>();
        thamSo.put("danh_muc_id", duLieu.danhMucId());
        thamSo.put("sku", duLieu.sku().strip());
        thamSo.put("ten", duLieu.ten().strip());
        thamSo.put("mo_ta_ngan", duLieu.moTaNgan().strip());
        thamSo.put("mo_ta", duLieu.moTa().strip());
        thamSo.put("gia", duLieu.gia());
        thamSo.put("gia_so_sanh", duLieu.giaSoSanh());
        thamSo.put("hien_thi", duLieu.hienThi());
        thamSo.put("kinh_doanh", duLieu.kinhDoanh());
        long sanPhamId;
        if (id == null) {
            sanPhamId = themSanPham.executeAndReturnKey(thamSo).longValue();
            jdbc.update("INSERT INTO ton_kho (san_pham_id) VALUES (?)", sanPhamId);
        } else {
            sanPhamId = id;
            if (jdbc.update("""
                UPDATE san_pham SET danh_muc_id=?,sku=?,ten=?,mo_ta_ngan=?,mo_ta=?,gia=?,gia_so_sanh=?,
                    hien_thi=?,kinh_doanh=? WHERE id=? AND NOT da_xoa
                """, duLieu.danhMucId(), duLieu.sku().strip(), duLieu.ten().strip(), duLieu.moTaNgan().strip(),
                duLieu.moTa().strip(), duLieu.gia(), duLieu.giaSoSanh(), duLieu.hienThi(), duLieu.kinhDoanh(), id) == 0) {
                throw khongTimThay();
            }
            jdbc.update("DELETE FROM hinh_anh_san_pham WHERE san_pham_id=?", id);
        }
        for (var anh : duLieu.hinhAnh()) {
            jdbc.update("INSERT INTO hinh_anh_san_pham (san_pham_id,duong_dan,mo_ta) VALUES (?,?,?)",
                sanPhamId, anh.duongDan(), anh.moTa());
        }
        return sanPhamId;
    }

    List<SanPham> yeuThich(long nguoiDungId) {
        return jdbc.query(COT_SAN_PHAM + " JOIN yeu_thich y ON y.san_pham_id=s.id WHERE " + CONG_KHAI
            + " AND y.nguoi_dung_id=? ORDER BY s.id DESC", DOC_SAN_PHAM, nguoiDungId);
    }

    @Transactional
    void datYeuThich(long nguoiDungId, long sanPhamId, boolean them) {
        if (them) {
            laySanPham(sanPhamId, false);
            jdbc.update("""
                INSERT INTO yeu_thich (nguoi_dung_id,san_pham_id) VALUES (?,?)
                ON DUPLICATE KEY UPDATE san_pham_id=san_pham_id
                """, nguoiDungId, sanPhamId);
        } else {
            jdbc.update("DELETE FROM yeu_thich WHERE nguoi_dung_id=? AND san_pham_id=?", nguoiDungId, sanPhamId);
        }
    }

    static void kiemTraUrl(String url) {
        try {
            var uri = URI.create(url);
            if ("https".equalsIgnoreCase(uri.getScheme()) && uri.getHost() != null && uri.getRawUserInfo() == null) {
                return;
            }
        } catch (IllegalArgumentException ignored) {
            // Thông báo thống nhất cho URL sai cú pháp hoặc sai giao thức.
        }
        throw khongHopLe("Đường dẫn tài nguyên phải là URL HTTPS hợp lệ.");
    }
}
