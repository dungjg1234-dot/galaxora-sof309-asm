package com.galaxora.cauhinh;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

import com.galaxora.taikhoan.DichVuTaiKhoan;

import jakarta.servlet.DispatcherType;

/**
 * Cấu hình bảo mật nền tảng.
 * Mặc định mọi yêu cầu đều phải xác thực; chỉ mở những đường dẫn công khai được liệt kê rõ.
 */
@Configuration
public class CauHinhBaoMat {

    @Bean
    SecurityFilterChain chuoiLocBaoMat(HttpSecurity http, DichVuTaiKhoan taiKhoan,
                                       SecurityContextRepository khoBaoMat, CsrfTokenRepository khoCsrf)
        throws Exception {
        http
            .securityContext(cauHinh -> cauHinh.securityContextRepository(khoBaoMat))
            .csrf(cauHinh -> cauHinh.csrfTokenRepository(khoCsrf))
            .requestCache(AbstractHttpConfigurer::disable)
            .addFilterBefore(new KiemTraPhienTaiKhoan(taiKhoan), AuthorizationFilter.class)
            .authorizeHttpRequests(quyen -> quyen
                .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()
                .requestMatchers("/api/quan-tri/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/kham-pha/noi-dung").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cua-hang/danh-muc", "/api/cua-hang/san-pham",
                    "/api/cua-hang/san-pham/*", "/api/cua-hang/san-pham/*/mo-hinh",
                    "/api/cua-hang/san-pham/*/mo-hinh/noi-dung", "/api/cua-hang/san-pham/*/danh-gia").permitAll()
                .requestMatchers("/api/cua-hang/yeu-thich/**", "/api/gio-hang/**", "/api/don-hang/**",
                    "/api/danh-gia/**", "/api/nhiem-vu/**", "/api/tai-khoan/dia-chi/**").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/he-thong/suc-khoe", "/api/tai-khoan/csrf").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/tai-khoan/dang-ky", "/api/tai-khoan/dang-nhap").permitAll()
                .anyRequest().authenticated())
            .logout(cauHinh -> cauHinh.logoutUrl("/api/tai-khoan/dang-xuat")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
            .exceptionHandling(loi -> loi
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        return http.build();
    }

    @Bean
    SecurityContextRepository khoNguCanhBaoMat() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    CsrfTokenRepository khoCsrf() {
        return new HttpSessionCsrfTokenRepository();
    }

    @Bean
    BCryptPasswordEncoder boBamMatKhau() {
        return new BCryptPasswordEncoder();
    }
}
