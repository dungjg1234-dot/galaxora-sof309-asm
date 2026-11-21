package com.galaxora.cauhinh;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.galaxora.taikhoan.DichVuTaiKhoan;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class KiemTraPhienTaiKhoan extends OncePerRequestFilter {
    private final DichVuTaiKhoan taiKhoan;

    KiemTraPhienTaiKhoan(DichVuTaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest yeuCau, HttpServletResponse phanHoi, FilterChain chuoi)
        throws ServletException, IOException {
        var xacThuc = SecurityContextHolder.getContext().getAuthentication();
        if (xacThuc != null && xacThuc.getPrincipal() instanceof DichVuTaiKhoan.Phien phien
            && !taiKhoan.phienConHieuLuc(phien)) {
            var session = yeuCau.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            SecurityContextHolder.clearContext();
        }
        chuoi.doFilter(yeuCau, phanHoi);
    }
}
