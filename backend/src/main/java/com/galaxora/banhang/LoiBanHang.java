package com.galaxora.banhang;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class LoiBanHang {
    private LoiBanHang() {}

    public static ResponseStatusException khongTimThay() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy dữ liệu.");
    }

    public static ResponseStatusException khongHopLe(String thongBao) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, thongBao);
    }

    public static ResponseStatusException xungDot(String thongBao) {
        return new ResponseStatusException(HttpStatus.CONFLICT, thongBao);
    }
}
