CREATE TABLE nhiem_vu (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    ten VARCHAR(160) NOT NULL,
    mo_ta VARCHAR(1000) NOT NULL,
    loai_dieu_kien VARCHAR(16) NOT NULL,
    ma_dieu_kien VARCHAR(48),
    xp INT NOT NULL CHECK (xp >= 0),
    ten_huy_hieu VARCHAR(160),
    co_voucher BOOLEAN NOT NULL,
    kich_hoat BOOLEAN NOT NULL,
    CHECK (xp > 0 OR ten_huy_hieu IS NOT NULL OR co_voucher),
    CHECK ((loai_dieu_kien='THIEN_THE' AND ma_dieu_kien IN
        ('mercury','venus','earth','mars','jupiter','saturn','uranus','neptune','moon','pluto'))
        OR (loai_dieu_kien='CHUONG' AND ma_dieu_kien IN ('1','2','3'))
        OR (loai_dieu_kien='DAU_HIEU_AN' AND ma_dieu_kien IS NULL)),
    CHECK (loai_dieu_kien='DAU_HIEU_AN' OR ma_dieu_kien IS NOT NULL)
);

CREATE TABLE nhiem_vu_hoan_thanh (
    nguoi_dung_id BIGINT NOT NULL,
    nhiem_vu_id BIGINT NOT NULL,
    hoan_thanh_luc DATETIME(6) NOT NULL DEFAULT (UTC_TIMESTAMP(6)),
    PRIMARY KEY (nguoi_dung_id, nhiem_vu_id),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (nhiem_vu_id) REFERENCES nhiem_vu(id)
);

CREATE TABLE cau_hinh_voucher_nhiem_vu (
    id BIGINT NOT NULL PRIMARY KEY,
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
    CHECK (loai <> 'PHAN_TRAM' OR gia_tri <= 100),
    FOREIGN KEY (id) REFERENCES nhiem_vu(id)
);
