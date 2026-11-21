CREATE TABLE dia_chi_nguoi_dung (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id BIGINT NOT NULL,
    nguoi_nhan VARCHAR(160) NOT NULL,
    so_dien_thoai VARCHAR(16) NOT NULL,
    dia_chi VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_dia_chi_chu_so_huu (nguoi_dung_id, id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);
