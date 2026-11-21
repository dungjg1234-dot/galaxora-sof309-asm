package com.galaxora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;

/**
 * Điểm khởi chạy của backend GALAXORA.
 * Tắt UserDetailsServiceAutoConfiguration để Spring không tự tạo tài khoản
 * mặc định với mật khẩu ngẫu nhiên; tài khoản thật sẽ làm ở Giai đoạn 3 (xác thực).
 */
@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class GalaxoraApplication {

    public static void main(String[] args) {
        SpringApplication.run(GalaxoraApplication.class, args);
    }
}
