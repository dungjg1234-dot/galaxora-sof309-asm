package com.galaxora.asm.ui.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.galaxora.asm.ui.pages.LoginPage;
import com.galaxora.asm.ui.support.BaseUiTest;
import com.galaxora.asm.ui.support.Config;

/**
 * Y4 - Selenium + TestNG: Đăng nhập, phân quyền và đăng xuất (TC-UI-LOG-01 -> TC-UI-LOG-06).
 */
public class LoginUiTest extends BaseUiTest {

    @Test(groups = {"smoke", "regression", "login"},
          description = "TC-UI-LOG-01: Đăng nhập ADMIN chuyển thẳng sang /quan-tri và thấy menu quản trị")
    public void adminLoginRedirectsToAdminPanel() {
        LoginPage page = new LoginPage(driver, wait);
        page.loginAsAdminAndWait();
        Assert.assertTrue(driver.getCurrentUrl().contains("/quan-tri"));
        Assert.assertTrue(driver.findElement(By.cssSelector("aside.qt-ben")).isDisplayed());
    }

    @Test(groups = {"smoke", "regression", "login"},
          description = "TC-UI-LOG-02: Đăng nhập USER thành công và xem được trang Tài khoản của bạn")
    public void userLoginShowsProfilePage() {
        LoginPage page = new LoginPage(driver, wait);
        page.loginAsUserAndWait();
        Assert.assertEquals(page.openProfileHeading(), "Tài khoản của bạn");
        Assert.assertTrue(driver.getPageSource().contains("Người dùng"));
    }

    @Test(groups = {"regression", "login"},
          description = "TC-UI-LOG-03: Đăng nhập sai mật khẩu ở lại trang đăng nhập và hiện cảnh báo lỗi chung")
    public void wrongPasswordShowsGenericError() {
        LoginPage page = new LoginPage(driver, wait).open();
        page.login(Config.userEmail(), "SaiMatKhau@999");
        String alert = page.waitErrorAlert();
        Assert.assertTrue(alert.contains("Không đăng nhập được"), "Thông báo lỗi không khớp: " + alert);
        Assert.assertTrue(driver.getCurrentUrl().contains("/tai-khoan/dang-nhap"));
    }

    @Test(groups = {"regression", "login"},
          description = "TC-UI-LOG-04: Đăng nhập email không tồn tại nhận cùng thông báo lỗi chung (chống dò tài khoản)")
    public void unknownEmailShowsSameGenericError() {
        LoginPage page = new LoginPage(driver, wait).open();
        page.login("khong-ton-tai-999@galaxora.test", "BatKy@123");
        String alert = page.waitErrorAlert();
        Assert.assertTrue(alert.contains("Không đăng nhập được"), "Thông báo lỗi không khớp: " + alert);
    }

    @Test(groups = {"smoke", "regression", "login"},
          description = "TC-UI-LOG-05: Khách chưa đăng nhập và USER thường đều bị chặn truy cập khu vực /quan-tri")
    public void unauthorizedAccessToAdminIsBlocked() {
        // 1. Chưa đăng nhập mở /quan-tri
        driver.get(Config.baseUrl() + "/quan-tri");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".qt-chan-truy-cap")));
        Assert.assertTrue(driver.findElements(By.cssSelector("aside.qt-ben")).isEmpty(),
            "Khách chưa đăng nhập không được thấy khung quản trị");

        // 2. Đăng nhập USER rồi mở /quan-tri
        LoginPage page = new LoginPage(driver, wait);
        page.loginAsUserAndWait();
        driver.get(Config.baseUrl() + "/quan-tri");
        String msg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".bh-loi[role='alert']")))
            .getText().trim();
        Assert.assertTrue(msg.contains("không có quyền truy cập"), "USER phải thấy thông báo từ chối quyền: " + msg);
    }

    @Test(groups = {"regression", "login"},
          description = "TC-UI-LOG-06: Đăng xuất hủy phiên và truy cập lại /tai-khoan/phien bị chuyển về đăng nhập")
    public void logoutInvalidatesSession() {
        LoginPage page = new LoginPage(driver, wait);
        page.loginAsUserAndWait();
        page.logoutFromProfile();
        Assert.assertTrue(page.waitStatusNotice().contains("Bạn đã đăng xuất"));

        driver.get(Config.baseUrl() + "/tai-khoan/phien");
        wait.until(ExpectedConditions.urlContains("/tai-khoan/dang-nhap"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/tai-khoan/dang-nhap"));
    }
}
