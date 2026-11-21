package com.galaxora.taikhoan;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DichVuTaiKhoan {
    private static final Pattern E164 = Pattern.compile("\\+[1-9][0-9]{1,14}");

    private final KhoTaiKhoan kho;
    private final GioiHanDangNhap gioiHan;
    private final BCryptPasswordEncoder bcrypt;
    private final String bamChoTaiKhoanKhongTonTai;

    DichVuTaiKhoan(KhoTaiKhoan kho, GioiHanDangNhap gioiHan, BCryptPasswordEncoder bcrypt) {
        this.kho = kho;
        this.gioiHan = gioiHan;
        this.bcrypt = bcrypt;
        this.bamChoTaiKhoanKhongTonTai = bcrypt.encode(UUID.randomUUID().toString());
    }

    public record Phien(long nguoiDungId, LocalDateTime capNhatLuc, List<String> vaiTro)
        implements java.io.Serializable {}

    @Transactional
    void dangKy(String hoTen, String email, String emailChuanHoa, String dienThoai, String matKhau) {
        String matKhauBam = bcrypt.encode(matKhau);
        kho.taoNguoiDung(hoTen, email, emailChuanHoa, dienThoai, matKhauBam, bayGio());
    }

    boolean choPhepDangNhapTuIp(String ip) {
        return gioiHan.nhanTheoIp(ip);
    }

    Optional<Phien> dangNhap(String dinhDanh, String matKhau) {
        var nguoiDung = dinhDanh.contains("@") ? kho.timEmail(dinhDanh) : kho.timDienThoai(dinhDanh);
        String khoa = nguoiDung.map(nguoi -> "id:" + nguoi.id()).orElse("khach:" + dinhDanh);
        if (!gioiHan.nhanTheoTaiKhoan(khoa)) {
            return Optional.empty();
        }
        String bam = nguoiDung.map(KhoTaiKhoan.NguoiDung::matKhauBam).orElse(bamChoTaiKhoanKhongTonTai);
        if (!bcrypt.matches(matKhau, bam) || nguoiDung.isEmpty()) {
            return Optional.empty();
        }
        var taiKhoan = nguoiDung.get();
        if (taiKhoan.dangKhoa()) {
            return Optional.empty();
        }
        gioiHan.xoaLoiTheoTaiKhoan(khoa);
        return Optional.of(new Phien(taiKhoan.id(), taiKhoan.capNhatLuc(), kho.vaiTro(taiKhoan.id())));
    }

    public boolean phienConHieuLuc(Phien phien) {
        return kho.tim(phien.nguoiDungId())
            .filter(nguoiDung -> !nguoiDung.dangKhoa()
                && phien.capNhatLuc().equals(nguoiDung.capNhatLuc()))
            .filter(nguoiDung -> phien.vaiTro().equals(kho.vaiTro(nguoiDung.id())))
            .isPresent();
    }

    @Transactional
    boolean doiMatKhau(Phien phien, String matKhau, String matKhauMoi) {
        var tk = kho.khoaTheoId(phien.nguoiDungId()).orElse(null);
        if (tk == null || tk.dangKhoa() || !tk.capNhatLuc().equals(phien.capNhatLuc())
            || !matKhauHopLe(matKhau) || !matKhauHopLe(matKhauMoi)
            || !bcrypt.matches(matKhau, tk.matKhauBam())) return false;
        var luc = bayGio();
        kho.capNhatMatKhau(tk.id(), bcrypt.encode(matKhauMoi), luc);
        return true;
    }

    static String chuanHoaEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    static String chuanHoaDienThoai(String dienThoai) {
        String giaTri = dienThoai.strip().replaceAll("[ ()-]", "");
        return E164.matcher(giaTri).matches() ? giaTri : "";
    }

    static boolean matKhauHopLe(String matKhau) {
        return !matKhau.isBlank() && matKhau.getBytes(StandardCharsets.UTF_8).length <= 72;
    }

    private static LocalDateTime bayGio() {
        return LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.MICROS);
    }
}
