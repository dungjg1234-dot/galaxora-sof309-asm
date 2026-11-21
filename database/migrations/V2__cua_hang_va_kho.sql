CREATE TABLE danh_muc (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ten VARCHAR(160) NOT NULL UNIQUE,
    hien_thi BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE san_pham (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    danh_muc_id BIGINT NOT NULL,
    sku VARCHAR(64) NOT NULL UNIQUE,
    ten VARCHAR(200) NOT NULL,
    mo_ta_ngan VARCHAR(500) NOT NULL,
    mo_ta TEXT NOT NULL,
    gia DECIMAL(15,2) NOT NULL CHECK (gia >= 0),
    gia_so_sanh DECIMAL(15,2) NULL CHECK (gia_so_sanh >= 0),
    hien_thi BOOLEAN NOT NULL DEFAULT TRUE,
    kinh_doanh BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    INDEX idx_san_pham_danh_muc (danh_muc_id, hien_thi, id),
    FOREIGN KEY (danh_muc_id) REFERENCES danh_muc(id)
);

CREATE TABLE hinh_anh_san_pham (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    san_pham_id BIGINT NOT NULL,
    duong_dan VARCHAR(2048) NOT NULL,
    mo_ta VARCHAR(250) NOT NULL,
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id),
    INDEX idx_hinh_anh_san_pham (san_pham_id, id)
);

CREATE TABLE yeu_thich (
    nguoi_dung_id BIGINT NOT NULL,
    san_pham_id BIGINT NOT NULL,
    PRIMARY KEY (nguoi_dung_id, san_pham_id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

CREATE TABLE mo_hinh_3d_san_pham (
    san_pham_id BIGINT NOT NULL PRIMARY KEY,
    ten_tep VARCHAR(255) NOT NULL,
    dinh_dang VARCHAR(4) NOT NULL CHECK (dinh_dang IN ('glb','gltf')),
    dung_luong BIGINT NOT NULL CHECK (dung_luong > 0),
    giay_phep VARCHAR(255) NOT NULL,
    nguon VARCHAR(2048) NOT NULL,
    tac_gia VARCHAR(250) NOT NULL,
    ghi_cong VARCHAR(500) NOT NULL,
    da_duyet BOOLEAN NOT NULL DEFAULT FALSE,
    noi_dung LONGBLOB NOT NULL,
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

CREATE TABLE cau_hinh_ban_hang (
    id TINYINT NOT NULL PRIMARY KEY CHECK (id = 1),
    nguong_ton_kho INT NULL CHECK (nguong_ton_kho >= 0),
    phi_van_chuyen DECIMAL(15,2) NULL CHECK (phi_van_chuyen >= 0)
);

INSERT INTO cau_hinh_ban_hang (id) VALUES (1);

CREATE TABLE ton_kho (
    san_pham_id BIGINT NOT NULL PRIMARY KEY,
    so_luong_thuc_te INT NOT NULL DEFAULT 0,
    so_luong_dang_giu INT NOT NULL DEFAULT 0,
    CHECK (so_luong_thuc_te >= so_luong_dang_giu AND so_luong_dang_giu >= 0),
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

CREATE TABLE phieu_nhap_kho (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id BIGINT NOT NULL,
    khoa_yeu_cau CHAR(36) NOT NULL UNIQUE,
    ly_do VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE chi_tiet_phieu_nhap (
    phieu_nhap_id BIGINT NOT NULL,
    san_pham_id BIGINT NOT NULL,
    so_luong INT NOT NULL CHECK (so_luong > 0),
    PRIMARY KEY (phieu_nhap_id, san_pham_id),
    FOREIGN KEY (phieu_nhap_id) REFERENCES phieu_nhap_kho(id),
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id)
);

CREATE TABLE giao_dich_kho (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    san_pham_id BIGINT NOT NULL,
    nguoi_dung_id BIGINT NOT NULL,
    loai VARCHAR(24) NOT NULL,
    thay_doi_thuc_te INT NOT NULL,
    thay_doi_dang_giu INT NOT NULL,
    thuc_te_sau INT NOT NULL,
    dang_giu_sau INT NOT NULL,
    ly_do VARCHAR(500) NOT NULL,
    phieu_nhap_id BIGINT NULL,
    khoa_yeu_cau CHAR(36) NULL UNIQUE,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    FOREIGN KEY (san_pham_id) REFERENCES san_pham(id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (phieu_nhap_id) REFERENCES phieu_nhap_kho(id),
    INDEX idx_giao_dich_kho_san_pham (san_pham_id, id)
);
