CREATE TABLE cau_hinh_voucher_hanh_trinh (
    id TINYINT NOT NULL PRIMARY KEY CHECK (id = 1),
    loai VARCHAR(12) NOT NULL CHECK (loai IN ('PHAN_TRAM','SO_TIEN')),
    gia_tri DECIMAL(15,2) NOT NULL CHECK (gia_tri > 0),
    giam_toi_da DECIMAL(15,2) CHECK (giam_toi_da >= 0),
    don_toi_thieu DECIMAL(15,2) CHECK (don_toi_thieu >= 0),
    bat_dau DATETIME(6) NOT NULL,
    ket_thuc DATETIME(6) NOT NULL,
    luot_toi_da INT NOT NULL,
    luot_da_cap INT NOT NULL DEFAULT 0,
    kich_hoat BOOLEAN NOT NULL,
    CHECK (ket_thuc > bat_dau AND luot_toi_da >= luot_da_cap AND luot_toi_da > 0),
    CHECK (loai <> 'PHAN_TRAM' OR gia_tri <= 100)
);

CREATE TABLE noi_dung_kham_pha (
    ma VARCHAR(48) NOT NULL PRIMARY KEY,
    ten VARCHAR(160) NOT NULL,
    mo_ta VARCHAR(1000) NOT NULL
);

CREATE TABLE noi_dung_nhiem_vu (
    ma VARCHAR(48) NOT NULL PRIMARY KEY,
    ten VARCHAR(160) NOT NULL,
    mo_ta VARCHAR(1000) NOT NULL
);

CREATE TABLE su_kien_kham_pha (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ten VARCHAR(160) NOT NULL,
    mo_ta VARCHAR(1000) NOT NULL,
    bat_dau DATETIME(6) NOT NULL,
    ket_thuc DATETIME(6) NOT NULL,
    hien_thi BOOLEAN NOT NULL,
    CHECK (ket_thuc > bat_dau)
);

CREATE TABLE nhat_ky_quan_tri (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    admin_id BIGINT NOT NULL,
    thao_tac VARCHAR(80) NOT NULL,
    duong_dan VARCHAR(300) NOT NULL,
    thoi_diem DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    INDEX (thoi_diem, id),
    INDEX (admin_id, thoi_diem),
    FOREIGN KEY (admin_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE doi_lien_he (
    nguoi_dung_id BIGINT NOT NULL PRIMARY KEY,
    loai VARCHAR(12) NOT NULL CHECK (loai IN ('EMAIL','DIEN_THOAI')),
    gia_tri VARCHAR(254) NOT NULL,
    ma_bam BINARY(32) NOT NULL,
    phat_hanh DATETIME(6) NOT NULL,
    het_han DATETIME(6) NOT NULL,
    so_lan_sai TINYINT NOT NULL DEFAULT 0,
    cap_nhat_tai_khoan DATETIME(6) NOT NULL,
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);
