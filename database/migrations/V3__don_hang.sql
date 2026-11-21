CREATE TABLE gio_hang (
    nguoi_dung_id BIGINT NOT NULL PRIMARY KEY,
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE chi_tiet_gio_hang (
    nguoi_dung_id BIGINT NOT NULL,
    san_pham_id BIGINT NOT NULL,
    so_luong INT NOT NULL CHECK (so_luong > 0),
    PRIMARY KEY (nguoi_dung_id, san_pham_id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES gio_hang(nguoi_dung_id),
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

CREATE TABLE voucher (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ma VARCHAR(40) NOT NULL UNIQUE,
    loai VARCHAR(12) NOT NULL CHECK (loai IN ('PHAN_TRAM', 'SO_TIEN')),
    gia_tri DECIMAL(15,2) NOT NULL CHECK (gia_tri > 0),
    giam_toi_da DECIMAL(15,2) NULL CHECK (giam_toi_da >= 0),
    don_toi_thieu DECIMAL(15,2) NULL CHECK (don_toi_thieu >= 0),
    bat_dau DATETIME(6) NOT NULL,
    ket_thuc DATETIME(6) NOT NULL,
    luot_toi_da INT NOT NULL,
    luot_da_dung INT NOT NULL DEFAULT 0,
    kich_hoat BOOLEAN NOT NULL,
    chu_so_huu_id BIGINT NULL,
    CHECK (luot_da_dung >= 0 AND luot_toi_da >= luot_da_dung AND ket_thuc > bat_dau),
    CHECK (loai <> 'PHAN_TRAM' OR gia_tri <= 100),
    FOREIGN KEY (chu_so_huu_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE don_hang (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ma VARCHAR(40) NOT NULL UNIQUE,
    nguoi_dung_id BIGINT NOT NULL,
    khoa_yeu_cau CHAR(36) NOT NULL,
    nguoi_nhan VARCHAR(160) NOT NULL,
    so_dien_thoai VARCHAR(32) NOT NULL,
    dia_chi VARCHAR(500) NOT NULL,
    ghi_chu VARCHAR(500) NOT NULL,
    tam_tinh DECIMAL(15,2) NOT NULL CHECK (tam_tinh >= 0),
    giam_gia DECIMAL(15,2) NOT NULL,
    phi_van_chuyen DECIMAL(15,2) NOT NULL CHECK (phi_van_chuyen >= 0),
    tong_tien DECIMAL(15,2) NOT NULL,
    phuong_thuc VARCHAR(3) NOT NULL DEFAULT 'COD' CHECK (phuong_thuc = 'COD'),
    trang_thai VARCHAR(24) NOT NULL CHECK (trang_thai IN ('CHO_XAC_NHAN','DA_XAC_NHAN','DANG_CHUAN_BI',
        'DANG_GIAO','DA_GIAO_THANH_CONG','DA_HUY','GIAO_THAT_BAI')),
    da_thu_tien BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    CHECK (giam_gia >= 0 AND giam_gia <= tam_tinh),
    CHECK (tong_tien = tam_tinh - giam_gia + phi_van_chuyen),
    CHECK (da_thu_tien = (trang_thai = 'DA_GIAO_THANH_CONG')),
    UNIQUE (nguoi_dung_id, khoa_yeu_cau),
    INDEX idx_don_hang_nguoi_dung (nguoi_dung_id, id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE chi_tiet_don_hang (
    don_hang_id BIGINT NOT NULL,
    san_pham_id BIGINT NOT NULL,
    sku VARCHAR(64) NOT NULL,
    ten_san_pham VARCHAR(200) NOT NULL,
    don_gia DECIMAL(15,2) NOT NULL CHECK (don_gia >= 0),
    so_luong INT NOT NULL CHECK (so_luong > 0),
    PRIMARY KEY (don_hang_id, san_pham_id),
    FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

CREATE TABLE lich_su_su_dung_voucher (
    don_hang_id BIGINT NOT NULL PRIMARY KEY,
    voucher_id BIGINT NOT NULL,
    ma VARCHAR(40) NOT NULL,
    giam_gia DECIMAL(15,2) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    FOREIGN KEY (voucher_id) REFERENCES voucher(id)
);

CREATE TABLE lich_su_trang_thai_don (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    don_hang_id BIGINT NOT NULL,
    nguoi_dung_id BIGINT NOT NULL,
    trang_thai VARCHAR(24) NOT NULL,
    ly_do VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    INDEX idx_lich_su_don (don_hang_id, id)
);

CREATE TABLE yeu_cau_huy_don (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    don_hang_id BIGINT NOT NULL,
    nguoi_dung_id BIGINT NOT NULL,
    trang_thai VARCHAR(12) NOT NULL CHECK (trang_thai IN ('CHO_DUYET','DA_DUYET','TU_CHOI')),
    dang_cho BIGINT AS (IF(trang_thai = 'CHO_DUYET', don_hang_id, NULL)) STORED UNIQUE,
    ly_do VARCHAR(500) NOT NULL,
    nguoi_xu_ly_id BIGINT NULL,
    ly_do_xu_ly VARCHAR(500) NULL,
    xu_ly_luc DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (nguoi_xu_ly_id) REFERENCES nguoi_dung(id)
);

ALTER TABLE giao_dich_kho
    ADD COLUMN don_hang_id BIGINT NULL,
    ADD FOREIGN KEY (don_hang_id) REFERENCES don_hang(id),
    ADD UNIQUE (don_hang_id, san_pham_id, loai);

CREATE TABLE danh_gia (
    don_hang_id BIGINT NOT NULL,
    san_pham_id BIGINT NOT NULL,
    nguoi_dung_id BIGINT NOT NULL,
    so_sao TINYINT NOT NULL CHECK (so_sao BETWEEN 1 AND 5),
    noi_dung VARCHAR(2000) NOT NULL,
    hien_thi BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (don_hang_id, san_pham_id, nguoi_dung_id),
    FOREIGN KEY (don_hang_id, san_pham_id) REFERENCES chi_tiet_don_hang(don_hang_id, san_pham_id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    INDEX idx_danh_gia_san_pham (san_pham_id, hien_thi, created_at)
);

CREATE TABLE lich_su_danh_gia (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    don_hang_id BIGINT NOT NULL,
    san_pham_id BIGINT NOT NULL,
    nguoi_dung_id BIGINT NOT NULL,
    so_sao TINYINT NOT NULL CHECK (so_sao BETWEEN 1 AND 5),
    noi_dung VARCHAR(2000) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (don_hang_id, san_pham_id, nguoi_dung_id)
        REFERENCES danh_gia(don_hang_id, san_pham_id, nguoi_dung_id)
);
