package com.galaxora.quantri;

import com.galaxora.taikhoan.DichVuTaiKhoan.Phien;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ThongBaoNguoiDung {
    private final JdbcTemplate jdbc;
    ThongBaoNguoiDung(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    record ThongBao(String noiDung, String duongDan) {}

    @GetMapping("/api/nhiem-vu/thong-bao")
    List<ThongBao> xem(@AuthenticationPrincipal Phien phien) {
        long id = phien.nguoiDungId();
        return jdbc.query("""
            SELECT noi_dung, duong_dan FROM (
              SELECT CONCAT('Đơn ',d.ma,' · ',CASE l.trang_thai
                WHEN 'DA_XAC_NHAN' THEN 'Đã xác nhận' WHEN 'DANG_CHUAN_BI' THEN 'Đang chuẩn bị'
                WHEN 'DANG_GIAO' THEN 'Đang giao' WHEN 'DA_GIAO_THANH_CONG' THEN 'Đã giao thành công'
                WHEN 'DA_HUY' THEN 'Đã hủy' ELSE 'Giao thất bại' END) noi_dung,
                CONCAT('/don-hang/',d.id) duong_dan,l.created_at thoi_diem
              FROM lich_su_trang_thai_don l JOIN don_hang d ON d.id=l.don_hang_id
              WHERE d.nguoi_dung_id=? AND l.trang_thai<>'CHO_XAC_NHAN'
              UNION ALL
              SELECT CONCAT('Yêu cầu hủy đơn ',d.ma,IF(y.trang_thai='DA_DUYET',' đã được duyệt',' đã bị từ chối')),
                CONCAT('/don-hang/',d.id),y.xu_ly_luc
              FROM yeu_cau_huy_don y JOIN don_hang d ON d.id=y.don_hang_id
              WHERE d.nguoi_dung_id=? AND y.trang_thai IN ('DA_DUYET','TU_CHOI')
              UNION ALL
              SELECT CASE ma WHEN 'KHAM_PHA' THEN 'Đã nhận huy hiệu Khám phá thiên thể'
                WHEN 'HANH_TRINH' THEN 'Đã hoàn thành hành trình Hệ Mặt Trời'
                WHEN 'VAT_THE_AN' THEN 'Đã nhận huy hiệu Nhiệm vụ bí mật'
                WHEN 'VOUCHER_HANH_TRINH' THEN 'Đã nhận voucher hành trình'
                ELSE 'Đã nhận voucher nhiệm vụ' END,
                '/tai-khoan/phien',nhan_luc
              FROM lich_su_phan_thuong WHERE nguoi_dung_id=? AND ma NOT REGEXP '^NV_[0-9]+$'
              UNION ALL
              SELECT CONCAT('Đã hoàn thành nhiệm vụ: ',n.ten),'/tai-khoan/phien',h.hoan_thanh_luc
              FROM nhiem_vu_hoan_thanh h JOIN nhiem_vu n ON n.id=h.nhiem_vu_id WHERE h.nguoi_dung_id=?
              UNION ALL
              SELECT CONCAT('Voucher ',ma,' sắp hết hạn'),'/tai-khoan/phien',UTC_TIMESTAMP(6)
              FROM voucher WHERE chu_so_huu_id=? AND kich_hoat AND luot_da_dung<luot_toi_da
                AND ket_thuc>UTC_TIMESTAMP(6) AND ket_thuc<=DATE_ADD(UTC_TIMESTAMP(6),INTERVAL 1 DAY)
              UNION ALL
              SELECT CONCAT('Sự kiện: ',ten,' · ',mo_ta),'/kham-pha',bat_dau FROM su_kien_kham_pha
              WHERE hien_thi AND bat_dau<=UTC_TIMESTAMP(6) AND ket_thuc>UTC_TIMESTAMP(6)
            ) tb ORDER BY thoi_diem DESC,noi_dung LIMIT 30
            """, (r,i) -> new ThongBao(r.getString(1),r.getString(2)),id,id,id,id,id);
    }
}
