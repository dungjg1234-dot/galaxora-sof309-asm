package com.galaxora.taikhoan;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class GioiHanDangNhap {
    private static final long KHOANG_THOI_GIAN = Duration.ofMinutes(15).toNanos();
    private static final int GIOI_HAN_TAI_KHOAN = 5;
    private static final int GIOI_HAN_IP = 30;
    private static final int SO_KHOA_TOI_DA = 10_000;

    private final Map<String, ArrayDeque<Long>> theoIp = new HashMap<>();
    private final Map<String, ArrayDeque<Long>> theoTaiKhoan = new HashMap<>();
    private long donDepTiepTheo = System.nanoTime();
    private Long lanChanIpGanNhat;
    private Long lanChanTaiKhoanGanNhat;

    synchronized boolean nhanTheoIp(String diaChi) {
        boolean choPhep = nhan(theoIp, diaChi, GIOI_HAN_IP);
        if (!choPhep) lanChanIpGanNhat = System.nanoTime();
        return choPhep;
    }

    synchronized boolean nhanTheoTaiKhoan(String khoa) {
        boolean choPhep = nhan(theoTaiKhoan, khoa, GIOI_HAN_TAI_KHOAN);
        if (!choPhep) lanChanTaiKhoanGanNhat = System.nanoTime();
        return choPhep;
    }

    public record CanhBao(boolean theoIp, boolean theoTaiKhoan) {}

    public synchronized CanhBao canhBaoGanDay() {
        long luc = System.nanoTime();
        return new CanhBao(lanChanIpGanNhat != null && luc - lanChanIpGanNhat < KHOANG_THOI_GIAN,
            lanChanTaiKhoanGanNhat != null && luc - lanChanTaiKhoanGanNhat < KHOANG_THOI_GIAN);
    }

    synchronized void xoaLoiTheoTaiKhoan(String khoa) {
        theoTaiKhoan.remove(khoa);
    }

    private boolean nhan(Map<String, ArrayDeque<Long>> luot, String khoa, int gioiHan) {
        long luc = System.nanoTime();
        if (luc - donDepTiepTheo >= 0) {
            theoIp.values().removeIf(muc -> luc - muc.getLast() >= KHOANG_THOI_GIAN);
            theoTaiKhoan.values().removeIf(muc -> luc - muc.getLast() >= KHOANG_THOI_GIAN);
            donDepTiepTheo = luc + Duration.ofMinutes(1).toNanos();
        }
        ArrayDeque<Long> hienTai = luot.get(khoa);
        if (hienTai == null) {
            if (luot.size() >= SO_KHOA_TOI_DA) {
                return false;
            }
            hienTai = new ArrayDeque<>();
            luot.put(khoa, hienTai);
        }
        while (!hienTai.isEmpty() && luc - hienTai.getFirst() >= KHOANG_THOI_GIAN) {
            hienTai.removeFirst();
        }
        if (hienTai.size() >= gioiHan) {
            return false;
        }
        hienTai.addLast(luc);
        return true;
    }
}
