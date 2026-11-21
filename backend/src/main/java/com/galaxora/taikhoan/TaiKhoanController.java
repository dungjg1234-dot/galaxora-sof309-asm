package com.galaxora.taikhoan;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/tai-khoan")
class TaiKhoanController {
    private final DichVuTaiKhoan taiKhoan;
    private final SecurityContextRepository khoBaoMat;
    private final CsrfAuthenticationStrategy doiCsrf;
    private final ChangeSessionIdAuthenticationStrategy doiIdPhien = new ChangeSessionIdAuthenticationStrategy();

    TaiKhoanController(DichVuTaiKhoan taiKhoan, SecurityContextRepository khoBaoMat,
                      CsrfTokenRepository khoCsrf) {
        this.taiKhoan = taiKhoan;
        this.khoBaoMat = khoBaoMat;
        this.doiCsrf = new CsrfAuthenticationStrategy(khoCsrf);
    }

    record DangKy(@NotBlank @Size(max = 255) String hoTen,
                  @NotBlank @Email @Size(max = 254) String email,
                  @NotBlank @Size(max = 32) String soDienThoai,
                  @NotBlank @Size(max = 72) String matKhau) {}

    record DangNhap(@NotBlank @Size(max = 254) String dinhDanh,
                    @NotBlank @Size(max = 72) String matKhau) {}

    record DoiMatKhau(@NotBlank @Size(max = 72) String matKhau, @NotBlank @Size(max = 72) String matKhauMoi) {}

    record ThongTinPhien(long nguoiDungId, List<String> vaiTro) {}

    record ThongTinCsrf(String tenHeader, String token) {}

    @GetMapping("/csrf")
    ThongTinCsrf csrf(CsrfToken token) {
        return new ThongTinCsrf(token.getHeaderName(), token.getToken());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    ResponseEntity<Void> duLieuKhongHopLe() {
        // Tránh ghi giá trị bị từ chối, có thể chứa mật khẩu.
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/dang-ky")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void dangKy(@Valid @RequestBody DangKy duLieu) {
        String dienThoai = DichVuTaiKhoan.chuanHoaDienThoai(duLieu.soDienThoai());
        if (dienThoai.isEmpty() || !DichVuTaiKhoan.matKhauHopLe(duLieu.matKhau())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            taiKhoan.dangKy(duLieu.hoTen().strip(), duLieu.email().strip(),
                emailChuanHoa(duLieu.email()), dienThoai, duLieu.matKhau());
        } catch (DuplicateKeyException e) {
            // Giữ cùng phản hồi cho email hoặc số điện thoại đã tồn tại.
        }
    }

    @PostMapping("/dang-nhap")
    ThongTinPhien dangNhap(@Valid @RequestBody DangNhap duLieu, HttpServletRequest yeuCau,
                          HttpServletResponse phanHoi) {
        if (!taiKhoan.choPhepDangNhapTuIp(yeuCau.getRemoteAddr())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        }
        String dinhDanh = duLieu.dinhDanh().contains("@") ? emailChuanHoa(duLieu.dinhDanh())
            : DichVuTaiKhoan.chuanHoaDienThoai(duLieu.dinhDanh());
        if (dinhDanh.isEmpty() || !DichVuTaiKhoan.matKhauHopLe(duLieu.matKhau())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        var phien = taiKhoan.dangNhap(dinhDanh, duLieu.matKhau())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        var vaiTro = phien.vaiTro().stream().map(ma -> new SimpleGrantedAuthority("ROLE_" + ma)).toList();
        var xacThuc = UsernamePasswordAuthenticationToken.authenticated(phien, null, vaiTro);
        doiIdPhien.onAuthentication(xacThuc, yeuCau, phanHoi);
        doiCsrf.onAuthentication(xacThuc, yeuCau, phanHoi);
        var nguCanh = SecurityContextHolder.createEmptyContext();
        nguCanh.setAuthentication(xacThuc);
        SecurityContextHolder.setContext(nguCanh);
        khoBaoMat.saveContext(nguCanh, yeuCau, phanHoi);
        return phien(phien);
    }

    @GetMapping("/phien")
    ThongTinPhien phien(@AuthenticationPrincipal DichVuTaiKhoan.Phien phien) {
        return new ThongTinPhien(phien.nguoiDungId(), phien.vaiTro());
    }

    @PostMapping("/doi-mat-khau")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void doiMatKhau(@AuthenticationPrincipal DichVuTaiKhoan.Phien phien, @Valid @RequestBody DoiMatKhau d,
                    HttpServletRequest yeuCau) {
        if (!taiKhoan.doiMatKhau(phien, d.matKhau(), d.matKhauMoi()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kiểm tra mật khẩu đã nhập.");
        var session = yeuCau.getSession(false);
        if (session != null) session.invalidate();
    }

    private static String emailChuanHoa(String email) {
        String chuanHoa = DichVuTaiKhoan.chuanHoaEmail(email);
        if (chuanHoa.length() > 254) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return chuanHoa;
    }

}
