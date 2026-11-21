package com.galaxora.quantri;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NhatKyQuanTri {
    private final JdbcTemplate jdbc;

    NhatKyQuanTri(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void ghiTrongGiaoDich(long adminId, String thaoTac, String duongDan) {
        jdbc.update("INSERT INTO nhat_ky_quan_tri(admin_id,thao_tac,duong_dan,thoi_diem) "
            + "VALUES (?,?,?,UTC_TIMESTAMP(6))", adminId, thaoTac, duongDan);
    }
}
