package com.galaxora.nhiemvu;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
class DichVuHoSoKhamPha {
    private final JdbcTemplate jdbc;

    DichVuHoSoKhamPha(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    record GhiNhanXp(String nguon, String maNoiDung, int soXp, LocalDateTime ghiNhanLuc) {}
    record PhanThuong(String ma, String loai, String maVoucher, LocalDateTime nhanLuc, String tenHuyHieu) {}
    record HoSo(long tongXp, CapDoKhamPha capDo, List<GhiNhanXp> lichSuXp, List<PhanThuong> phanThuong) {}

    @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
    HoSo xem(long nguoiDungId) {
        List<GhiNhanXp> lichSu = jdbc.query("""
            SELECT nguon,ma_noi_dung,so_xp,ghi_nhan_luc FROM lich_su_xp
            WHERE nguoi_dung_id=? ORDER BY id DESC
            """, (r, i) -> new GhiNhanXp(r.getString(1), r.getString(2), r.getInt(3),
                r.getObject(4, LocalDateTime.class)), nguoiDungId);
        List<PhanThuong> phanThuong = jdbc.query("""
            SELECT p.ma,p.loai,v.ma,p.nhan_luc,n.ten_huy_hieu
            FROM lich_su_phan_thuong p LEFT JOIN voucher v ON v.id=p.voucher_id AND v.chu_so_huu_id=p.nguoi_dung_id
            LEFT JOIN nhiem_vu_hoan_thanh h ON h.nguoi_dung_id=p.nguoi_dung_id AND p.ma=CONCAT('NV_',h.nhiem_vu_id)
            LEFT JOIN nhiem_vu n ON n.id=h.nhiem_vu_id
            WHERE p.nguoi_dung_id=? ORDER BY p.nhan_luc DESC,p.ma
            """, (r, i) -> new PhanThuong(r.getString(1), r.getString(2), r.getString(3),
                r.getObject(4, LocalDateTime.class), r.getString(5)), nguoiDungId);
        long tongXp = lichSu.stream().mapToLong(GhiNhanXp::soXp).sum();
        return new HoSo(tongXp, CapDoKhamPha.tuTongXp(tongXp), lichSu, phanThuong);
    }
}
