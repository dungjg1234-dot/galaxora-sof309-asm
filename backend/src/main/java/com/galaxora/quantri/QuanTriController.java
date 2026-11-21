package com.galaxora.quantri;

import static com.galaxora.banhang.LoiBanHang.khongTimThay;
import static com.galaxora.banhang.LoiBanHang.xungDot;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import com.galaxora.taikhoan.GioiHanDangNhap;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
class QuanTriController {
    private static final Set<String> THIEN_THE = Set.of("sun","mercury","venus","earth","moon","mars","jupiter",
        "saturn","uranus","neptune","pluto","thien-ha","tinh-van","ho-den");
    private static final Set<String> NHIEM_VU = Set.of("KHAM_PHA","HANH_TRINH","VAT_THE_AN","CHUONG_1","CHUONG_2",
        "CHUONG_3","HUY_HIEU_KHAM_PHA","HUY_HIEU_HANH_TRINH","HUY_HIEU_VAT_THE_AN");
    private final JdbcTemplate jdbc;
    private final GioiHanDangNhap gioiHanDangNhap;
    private final NhatKyQuanTri nhatKy;

    QuanTriController(JdbcTemplate jdbc, GioiHanDangNhap gioiHanDangNhap, NhatKyQuanTri nhatKy) {
        this.jdbc = jdbc;
        this.gioiHanDangNhap = gioiHanDangNhap;
        this.nhatKy = nhatKy;
    }

    record TongQuan(long donCanXuLy, long yeuCauHuy, long tonKhoThap, BigDecimal doanhThu, long nhiemVuHoanThanh) {}
    record ThongBao(String noiDung, String duongDan) {}
    record NoiDung(String ma, String ten, String moTa) {}
    record SuaNoiDung(@NotBlank @Size(max=160) String ten, @NotBlank @Size(max=1000) String moTa) {}
    record DongNhatKy(long id, long adminId, String thaoTac, String duongDan, LocalDateTime thoiDiem) {}
    record TaiKhoan(long id, String hoTen, String email, boolean dangKhoa) {}
    record DatKhoa(@NotNull Boolean khoa) {}
    record SuKien(long id, String ten, String moTa, LocalDateTime batDau, LocalDateTime ketThuc, boolean hienThi) {}
    record LuuSuKien(@NotBlank @Size(max=160) String ten, @NotBlank @Size(max=1000) String moTa,
        @NotNull LocalDateTime batDau, @NotNull LocalDateTime ketThuc, @NotNull Boolean hienThi) {}

    @GetMapping("/api/quan-tri/tong-quan")
    TongQuan tongQuan() {
        return jdbc.queryForObject("""
            SELECT
              (SELECT COUNT(*) FROM don_hang WHERE trang_thai IN ('CHO_XAC_NHAN','DA_XAC_NHAN','DANG_CHUAN_BI','DANG_GIAO')),
              (SELECT COUNT(*) FROM yeu_cau_huy_don WHERE trang_thai='CHO_DUYET'),
              (SELECT COUNT(*) FROM ton_kho t JOIN san_pham s ON s.id=t.san_pham_id CROSS JOIN cau_hinh_ban_hang c
                WHERE s.hien_thi AND (t.so_luong_thuc_te-t.so_luong_dang_giu=0
                OR t.so_luong_thuc_te-t.so_luong_dang_giu<=c.nguong_ton_kho)),
              (SELECT COALESCE(SUM(tong_tien),0) FROM don_hang WHERE trang_thai='DA_GIAO_THANH_CONG'),
              (SELECT COUNT(*) FROM lich_su_phan_thuong WHERE loai='HUY_HIEU' AND ma IN ('KHAM_PHA','HANH_TRINH','VAT_THE_AN'))
                + (SELECT COUNT(*) FROM nhiem_vu_hoan_thanh)
            """, (r,i) -> new TongQuan(r.getLong(1),r.getLong(2),r.getLong(3),r.getBigDecimal(4),r.getLong(5)));
    }

    @GetMapping("/api/quan-tri/thong-bao")
    List<ThongBao> thongBao() {
        var d = tongQuan();
        var kq = new ArrayList<ThongBao>();
        if (d.donCanXuLy() > 0) kq.add(new ThongBao(d.donCanXuLy() + " đơn đang cần xử lý", "/quan-tri/don-hang"));
        if (d.yeuCauHuy() > 0) kq.add(new ThongBao(d.yeuCauHuy() + " yêu cầu hủy đang chờ", "/quan-tri/don-hang"));
        if (d.tonKhoThap() > 0) kq.add(new ThongBao(d.tonKhoThap() + " sản phẩm sắp hết hoặc hết hàng", "/quan-tri/kho"));
        Long danhGia = jdbc.queryForObject("SELECT COUNT(*) FROM danh_gia WHERE created_at>=UTC_DATE()", Long.class);
        if (danhGia != null && danhGia > 0) kq.add(new ThongBao(danhGia + " đánh giá mới hôm nay", "/quan-tri/cua-hang"));
        var canhBao = gioiHanDangNhap.canhBaoGanDay();
        if (canhBao.theoTaiKhoan()) kq.add(new ThongBao("Đăng nhập chạm giới hạn theo tài khoản trong 15 phút qua", "/quan-tri"));
        if (canhBao.theoIp()) kq.add(new ThongBao("Đăng nhập chạm giới hạn theo địa chỉ IP trong 15 phút qua", "/quan-tri"));
        return kq;
    }

    @GetMapping("/api/quan-tri/su-kien")
    List<SuKien> suKien() {
        return jdbc.query("""
            SELECT id,ten,mo_ta,bat_dau,ket_thuc,hien_thi FROM su_kien_kham_pha ORDER BY bat_dau DESC,id DESC
            """, (r,i) -> new SuKien(r.getLong(1),r.getString(2),r.getString(3),
                r.getObject(4,LocalDateTime.class),r.getObject(5,LocalDateTime.class),r.getBoolean(6)));
    }

    @PostMapping("/api/quan-tri/su-kien")
    @Transactional
    ResponseEntity<Void> taoSuKien(@AuthenticationPrincipal Phien phien, @Valid @RequestBody LuuSuKien d) {
        kiemTraSuKien(d);
        jdbc.update("INSERT INTO su_kien_kham_pha(ten,mo_ta,bat_dau,ket_thuc,hien_thi) VALUES (?,?,?,?,?)",
            d.ten().strip(),d.moTa().strip(),d.batDau(),d.ketThuc(),d.hienThi());
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Tạo sự kiện", "/api/quan-tri/su-kien");
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/api/quan-tri/su-kien/{id}")
    @Transactional
    ResponseEntity<Void> suaSuKien(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                                 @Valid @RequestBody LuuSuKien d) {
        kiemTraSuKien(d);
        if (jdbc.update("UPDATE su_kien_kham_pha SET ten=?,mo_ta=?,bat_dau=?,ket_thuc=?,hien_thi=? WHERE id=?",
            d.ten().strip(),d.moTa().strip(),d.batDau(),d.ketThuc(),d.hienThi(),id)==0) throw khongTimThay();
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Sửa sự kiện", "/api/quan-tri/su-kien/" + id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/su-kien/{id}")
    @Transactional
    ResponseEntity<Void> xoaSuKien(@AuthenticationPrincipal Phien phien, @PathVariable long id) {
        if (jdbc.update("DELETE FROM su_kien_kham_pha WHERE id=?", id) == 0) throw khongTimThay();
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa sự kiện", "/api/quan-tri/su-kien/" + id);
        return ResponseEntity.noContent().build();
    }

    private static void kiemTraSuKien(LuuSuKien d) {
        if (!d.ketThuc().isAfter(d.batDau()))
            throw com.galaxora.banhang.LoiBanHang.khongHopLe("Thời điểm kết thúc phải sau thời điểm bắt đầu.");
    }

    @GetMapping("/api/quan-tri/nhat-ky")
    List<DongNhatKy> nhatKy(
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate tu,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate den,
        @RequestParam(required=false) @Size(max=80) String thaoTac,
        @RequestParam(required=false) @Positive Long adminId,
        @RequestParam(defaultValue="0") @Min(0) @Max(100000) int trang) {
        return jdbc.query("""
            SELECT id,admin_id,thao_tac,duong_dan,thoi_diem FROM nhat_ky_quan_tri
            WHERE (COALESCE(?,thoi_diem)<=thoi_diem) AND (COALESCE(?,DATE_ADD(thoi_diem,INTERVAL 1 DAY))>thoi_diem)
                AND admin_id=COALESCE(?,admin_id) AND thao_tac LIKE CONCAT('%',COALESCE(?,''),'%')
            ORDER BY thoi_diem DESC,id DESC LIMIT 50 OFFSET ?
            """, (r,i) -> new DongNhatKy(r.getLong(1),r.getLong(2),r.getString(3),r.getString(4),
                r.getObject(5, LocalDateTime.class)), tu == null ? null : tu.atStartOfDay(),
            den == null ? null : den.plusDays(1).atStartOfDay(), adminId, thaoTac, trang * 50);
    }

    @GetMapping("/api/quan-tri/tai-khoan")
    List<TaiKhoan> taiKhoan(@RequestParam(defaultValue="") @Size(max=100) String tuKhoa,
                            @RequestParam(defaultValue="0") @Min(0) @Max(100000) int trang) {
        return jdbc.query("""
            SELECT n.id,n.ho_ten,n.email,n.khoa_luc IS NOT NULL FROM nguoi_dung n
            JOIN nguoi_dung_vai_tro nv ON nv.nguoi_dung_id=n.id JOIN vai_tro v ON v.id=nv.vai_tro_id
            WHERE v.ma='USER' AND (LOCATE(?,n.ho_ten)>0 OR LOCATE(?,n.email)>0 OR LOCATE(?,n.so_dien_thoai)>0)
            ORDER BY n.id DESC LIMIT 50 OFFSET ?
            """, (r,i) -> new TaiKhoan(r.getLong(1),r.getString(2),r.getString(3),r.getBoolean(4)),
            tuKhoa.strip(),tuKhoa.strip(),tuKhoa.strip(),trang*50);
    }

    @PutMapping("/api/quan-tri/tai-khoan/{id}/khoa")
    @Transactional
    public ResponseEntity<Void> khoa(@AuthenticationPrincipal Phien phien, @PathVariable long id,
                                    @Valid @RequestBody DatKhoa d) {
        if (id==phien.nguoiDungId()) throw xungDot("Không thể tự khóa tài khoản đang quản trị.");
        if (jdbc.update("""
            UPDATE nguoi_dung n SET khoa_luc=IF(?,UTC_TIMESTAMP(6),NULL),updated_at=UTC_TIMESTAMP(6)
            WHERE n.id=? AND EXISTS(SELECT 1 FROM nguoi_dung_vai_tro nv JOIN vai_tro v ON v.id=nv.vai_tro_id
                WHERE nv.nguoi_dung_id=n.id AND v.ma='USER')
              AND NOT EXISTS(SELECT 1 FROM nguoi_dung_vai_tro nv JOIN vai_tro v ON v.id=nv.vai_tro_id
                WHERE nv.nguoi_dung_id=n.id AND v.ma='ADMIN')
            """, d.khoa(),id)==0) throw khongTimThay();
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), d.khoa() ? "Khóa tài khoản" : "Mở khóa tài khoản",
            "/api/quan-tri/tai-khoan/" + id + "/khoa");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/kham-pha/noi-dung")
    List<NoiDung> khamPha() { return noiDung("noi_dung_kham_pha"); }

    @GetMapping("/api/nhiem-vu/noi-dung")
    List<NoiDung> nhiemVu(@AuthenticationPrincipal Phien phien) {
        boolean daTim = Boolean.TRUE.equals(jdbc.queryForObject("""
            SELECT EXISTS(SELECT 1 FROM lich_su_phan_thuong WHERE nguoi_dung_id=? AND ma='VAT_THE_AN')
            """, Boolean.class, phien.nguoiDungId()));
        return noiDung("noi_dung_nhiem_vu").stream()
            .filter(d -> daTim || !(d.ma().equals("VAT_THE_AN") || d.ma().equals("HUY_HIEU_VAT_THE_AN"))).toList();
    }

    @GetMapping("/api/quan-tri/noi-dung/{loai}")
    List<NoiDung> xem(@PathVariable String loai) { return noiDung(bang(loai)); }

    @PutMapping("/api/quan-tri/noi-dung/{loai}/{ma}")
    @Transactional
    ResponseEntity<Void> sua(@AuthenticationPrincipal Phien phien, @PathVariable String loai,
                            @PathVariable String ma, @Valid @RequestBody SuaNoiDung d) {
        String bang = bang(loai);
        if (!(loai.equals("kham-pha") ? THIEN_THE : NHIEM_VU).contains(ma)) throw khongTimThay();
        jdbc.update("INSERT INTO " + bang + "(ma,ten,mo_ta) VALUES (?,?,?) AS moi "
            + "ON DUPLICATE KEY UPDATE ten=moi.ten,mo_ta=moi.mo_ta", ma, d.ten().strip(), d.moTa().strip());
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Sửa nội dung " + (loai.equals("kham-pha") ? "khám phá" : "nhiệm vụ"),
            "/api/quan-tri/noi-dung/" + loai + "/" + ma);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/quan-tri/noi-dung/{loai}/{ma}")
    @Transactional
    ResponseEntity<Void> xoaNoiDung(@AuthenticationPrincipal Phien phien, @PathVariable String loai, @PathVariable String ma) {
        if (jdbc.update("DELETE FROM " + bang(loai) + " WHERE ma=?", ma) == 0) throw khongTimThay();
        nhatKy.ghiTrongGiaoDich(phien.nguoiDungId(), "Xóa nội dung tùy chỉnh", "/api/quan-tri/noi-dung/" + loai + "/" + ma);
        return ResponseEntity.noContent().build();
    }

    private static String bang(String loai) {
        return switch (loai) {
            case "kham-pha" -> "noi_dung_kham_pha";
            case "nhiem-vu" -> "noi_dung_nhiem_vu";
            default -> throw khongTimThay();
        };
    }

    private List<NoiDung> noiDung(String bang) {
        return jdbc.query("SELECT ma,ten,mo_ta FROM " + bang + " ORDER BY ma",
            (r,i) -> new NoiDung(r.getString(1),r.getString(2),r.getString(3)));
    }
}
