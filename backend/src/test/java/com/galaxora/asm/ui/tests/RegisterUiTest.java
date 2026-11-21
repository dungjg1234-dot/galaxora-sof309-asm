package com.galaxora.asm.ui.tests;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.galaxora.asm.ui.pages.LoginPage;
import com.galaxora.asm.ui.pages.RegisterPage;
import com.galaxora.asm.ui.support.BaseUiTest;

/**
 * Y4 - Selenium + TestNG: Kịch bản Đăng ký tài khoản trên giao diện /tai-khoan/dang-ky (TC-UI-REG-01 -> TC-UI-REG-05).
 */
public class RegisterUiTest extends BaseUiTest {

    @Test(groups = {"smoke", "regression", "register"},
          description = "TC-UI-REG-01: Trang đăng ký hiển thị đầy đủ 6 trường nhập liệu và nút Tạo tài khoản")
    public void registerFormDisplaysRequiredFields() {
        RegisterPage page = new RegisterPage(driver, wait).open();
        Assert.assertTrue(page.hasAllFields(), "Trang đăng ký phải hiển thị đầy đủ các trường nhập liệu");
        Assert.assertEquals(page.submitButtonText(), "Tạo tài khoản");
    }

    @Test(groups = {"smoke", "regression", "register"},
          description = "TC-UI-REG-02: Đăng ký tài khoản hợp lệ thành công và đăng nhập được ngay")
    public void registerValidUserThenLogin() {
        long ts = System.currentTimeMillis() % 1_000_000L;
        String email = "reg-ui-" + ts + "@galaxora.test";
        String phone = String.format("091%07d", ts);
        String pass = "GalaxoraPass@2026";

        RegisterPage reg = new RegisterPage(driver, wait).open();
        reg.fill("Lương Hoàng Dũng UI", email, "VN", phone, pass, pass).submit();

        wait.until(ExpectedConditions.urlContains("/tai-khoan/dang-nhap"));
        String notice = reg.waitStatusNotice();
        Assert.assertTrue(notice.contains("Yêu cầu đăng ký đã được tiếp nhận"),
            "Thông báo tiếp nhận không khớp: " + notice);

        LoginPage login = new LoginPage(driver, wait);
        login.login(email, pass);
        wait.until(d -> !d.getCurrentUrl().contains("/tai-khoan/dang-nhap"));
        Assert.assertEquals(login.openProfileHeading(), "Tài khoản của bạn");
    }

    @DataProvider(name = "invalidHtml5Inputs")
    public Object[][] invalidHtml5Inputs() {
        return new Object[][] {
            {"", "valid1@galaxora.test", "0912345678", "MatKhau@123", "MatKhau@123"},
            {"Người Dùng", "", "0912345678", "MatKhau@123", "MatKhau@123"},
            {"Người Dùng", "khong-co-a-cong", "0912345678", "MatKhau@123", "MatKhau@123"},
            {"Người Dùng", "valid2@galaxora.test", "", "MatKhau@123", "MatKhau@123"},
            {"Người Dùng", "valid3@galaxora.test", "0912345678", "", ""}
        };
    }

    @Test(groups = {"regression", "register"}, dataProvider = "invalidHtml5Inputs",
          description = "TC-UI-REG-03: Trình duyệt chặn submit khi thiếu trường bắt buộc hoặc sai định dạng email")
    public void invalidHtml5InputBlockedByBrowser(String name, String email, String phone, String pass, String confirm) {
        RegisterPage reg = new RegisterPage(driver, wait).open();
        reg.fill(name, email, "VN", phone, pass, confirm).submit();
        Assert.assertFalse(reg.isFormValid(), "Biểu mẫu phải không hợp lệ theo HTML5 constraint validation");
        Assert.assertTrue(driver.getCurrentUrl().contains("/tai-khoan/dang-ky"),
            "Không được rời khỏi trang đăng ký khi dữ liệu sai");
    }

    @Test(groups = {"regression", "register"},
          description = "TC-UI-REG-04: Mật khẩu xác nhận không khớp và số điện thoại sai chuẩn VN bị báo lỗi rõ ràng")
    public void mismatchPasswordAndInvalidPhoneShowAlerts() {
        RegisterPage reg = new RegisterPage(driver, wait).open();
        reg.fill("Test Lỗi", "err1@galaxora.test", "VN", "0912345678", "MatKhau@123", "KhacMatKhau").submit();
        Assert.assertTrue(reg.waitErrorAlert().contains("Mật khẩu xác nhận chưa khớp"),
            "Phải báo lỗi mật khẩu xác nhận chưa khớp");

        reg.fill("Test Lỗi", "err2@galaxora.test", "VN", "1234", "MatKhau@123", "MatKhau@123").submit();
        Assert.assertTrue(reg.waitErrorAlert().contains("Kiểm tra lại số điện thoại"),
            "Phải báo lỗi số điện thoại không hợp lệ theo quốc gia đã chọn");
    }

    @Test(groups = {"regression", "register"},
          description = "TC-UI-REG-05: Đăng ký trùng email (khác hoa thường) vẫn hiện thông báo chung, không tiết lộ tài khoản tồn tại")
    public void duplicateEmailShowsGenericMessage() {
        long ts = (System.currentTimeMillis() + 17) % 1_000_000L;
        String email = "dup-ui-" + ts + "@galaxora.test";
        String phone1 = String.format("092%07d", ts);
        String phone2 = String.format("093%07d", ts);

        RegisterPage reg = new RegisterPage(driver, wait).open();
        reg.fill("Người Dùng 1", email, "VN", phone1, "MatKhau@123", "MatKhau@123").submit();
        wait.until(ExpectedConditions.urlContains("/tai-khoan/dang-nhap"));

        reg.open();
        reg.fill("Người Dùng 2", email.toUpperCase(), "VN", phone2, "MatKhau@123", "MatKhau@123").submit();
        wait.until(ExpectedConditions.urlContains("/tai-khoan/dang-nhap"));
        String notice = reg.waitStatusNotice();
        Assert.assertTrue(notice.contains("Yêu cầu đăng ký đã được tiếp nhận"));
        Assert.assertFalse(notice.toLowerCase().contains("đã tồn tại"),
            "Không được tiết lộ sự tồn tại của email đã đăng ký");
    }
}
