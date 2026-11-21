CREATE TABLE nguoi_dung (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ho_ten VARCHAR(255) NOT NULL,
    email VARCHAR(254) NOT NULL,
    email_chuan_hoa VARCHAR(254) COLLATE utf8mb4_0900_bin NOT NULL,
    so_dien_thoai VARCHAR(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    mat_khau_bam CHAR(60) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    xac_minh_email_luc DATETIME(6),
    khoa_luc DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_nguoi_dung_email UNIQUE (email_chuan_hoa),
    CONSTRAINT uq_nguoi_dung_dien_thoai UNIQUE (so_dien_thoai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE vai_tro (
    id BIGINT NOT NULL AUTO_INCREMENT,
    ma VARCHAR(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_vai_tro_ma UNIQUE (ma),
    CONSTRAINT ck_vai_tro_ma CHECK (CAST(ma AS BINARY) IN ('USER', 'ADMIN'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE nguoi_dung_vai_tro (
    nguoi_dung_id BIGINT NOT NULL,
    vai_tro_id BIGINT NOT NULL,
    PRIMARY KEY (nguoi_dung_id, vai_tro_id),
    INDEX ix_nguoi_dung_vai_tro_vai_tro (vai_tro_id),
    CONSTRAINT fk_nguoi_dung_vai_tro_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id) ON DELETE RESTRICT,
    CONSTRAINT fk_nguoi_dung_vai_tro_vai_tro
        FOREIGN KEY (vai_tro_id) REFERENCES vai_tro (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE ma_xac_minh (
    nguoi_dung_id BIGINT NOT NULL,
    muc_dich VARCHAR(24) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
    gia_tri_bam BINARY(32) NOT NULL,
    so_lan_sai TINYINT UNSIGNED NOT NULL DEFAULT 0,
    phat_hanh_luc DATETIME(6) NOT NULL,
    het_han_luc DATETIME(6) NOT NULL,
    da_dung_luc DATETIME(6),
    PRIMARY KEY (nguoi_dung_id, muc_dich),
    CONSTRAINT uq_ma_xac_minh_gia_tri UNIQUE (muc_dich, gia_tri_bam),
    CONSTRAINT fk_ma_xac_minh_nguoi_dung
        FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung (id) ON DELETE RESTRICT,
    CONSTRAINT ck_ma_xac_minh_muc_dich
        CHECK (CAST(muc_dich AS BINARY) IN ('XAC_MINH_EMAIL', 'DAT_LAI_MAT_KHAU')),
    CONSTRAINT ck_ma_xac_minh_so_lan_sai
        CHECK (so_lan_sai <= 5 AND (muc_dich <> 'DAT_LAI_MAT_KHAU' OR so_lan_sai = 0)),
    CONSTRAINT ck_ma_xac_minh_het_han CHECK (het_han_luc > phat_hanh_luc),
    CONSTRAINT ck_ma_xac_minh_da_dung
        CHECK (da_dung_luc IS NULL OR (da_dung_luc >= phat_hanh_luc AND da_dung_luc < het_han_luc))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO vai_tro (ma) VALUES ('USER'), ('ADMIN');
