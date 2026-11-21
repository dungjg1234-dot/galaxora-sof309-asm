CREATE TABLE lich_su_xp (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nguoi_dung_id BIGINT NOT NULL,
    nguon VARCHAR(24) NOT NULL,
    ma_noi_dung VARCHAR(48) NOT NULL,
    so_xp INT NOT NULL CHECK (so_xp <> 0),
    ghi_nhan_luc DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    UNIQUE (nguoi_dung_id, nguon, ma_noi_dung),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id)
);

CREATE TABLE lich_su_phan_thuong (
    nguoi_dung_id BIGINT NOT NULL,
    ma VARCHAR(48) NOT NULL,
    loai VARCHAR(12) NOT NULL,
    voucher_id BIGINT NULL,
    nhan_luc DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (nguoi_dung_id, ma),
    UNIQUE (voucher_id),
    CHECK ((loai = 'HUY_HIEU' AND voucher_id IS NULL)
        OR (loai = 'VOUCHER' AND voucher_id IS NOT NULL)),
    FOREIGN KEY (nguoi_dung_id) REFERENCES nguoi_dung(id),
    FOREIGN KEY (voucher_id) REFERENCES voucher(id)
);
