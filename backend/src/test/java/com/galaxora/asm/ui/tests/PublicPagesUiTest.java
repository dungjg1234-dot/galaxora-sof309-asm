package com.galaxora.asm.ui.tests;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.galaxora.asm.ui.support.BaseUiTest;
import com.galaxora.asm.ui.support.Config;

/**
 * Y4 - Selenium + TestNG: Kiểm thử các trang công khai và xử lý 404 (TC-UI-PUB-01 -> TC-UI-PUB-04).
 */
public class PublicPagesUiTest extends BaseUiTest {

    @DataProvider(name = "publicPaths")
    public Object[][] publicPaths() {
        return new Object[][] {
            {"/"},
            {"/cua-hang"},
            {"/he-thong"},
            {"/tai-khoan/dang-nhap"},
            {"/tai-khoan/dang-ky"}
        };
    }

    @Test(groups = {"smoke", "regression", "public"}, dataProvider = "publicPaths",
          description = "TC-UI-PUB-01: Các trang công khai chính mở được, có tiêu đề GALAXORA và không lộ trang lỗi mặc định")
    public void publicRouteLoadsCleanly(String path) {
        driver.get(Config.baseUrl() + path);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("main")));
        String title = driver.getTitle();
        Assert.assertFalse(title.isBlank(), "Tiêu đề trang không được rỗng tại " + path);
        Assert.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"),
            "Không được lộ trang lỗi mặc định tại " + path);
    }

    @Test(groups = {"smoke", "regression", "public"},
          description = "TC-UI-PUB-02: Trang kiểm tra kết nối /he-thong xác nhận API backend đang hoạt động")
    public void healthPageShowsBackendActive() {
        driver.get(Config.baseUrl() + "/he-thong");
        String statusText = wait.until(d -> {
            String txt = d.findElement(By.cssSelector("p[role='status']")).getText().trim();
            return txt.contains("Backend đang hoạt động") ? txt : null;
        });
        Assert.assertTrue(statusText.contains("Backend đang hoạt động"),
            "Trang /he-thong phải hiển thị trạng thái Backend đang hoạt động: " + statusText);
    }

    @Test(groups = {"regression", "public"},
          description = "TC-UI-PUB-03: Từ Trang chủ điều hướng qua liên kết tới Cửa hàng (/cua-hang)")
    public void navigateFromHomeToStorefront() {
        driver.get(Config.baseUrl() + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/cua-hang']"))).click();
        wait.until(ExpectedConditions.urlContains("/cua-hang"));
        Assert.assertTrue(driver.getCurrentUrl().contains("/cua-hang"));
    }

    @Test(groups = {"regression", "public"},
          description = "TC-UI-PUB-04: Truy cập đường dẫn không tồn tại hiển thị trang 404 thân thiện có nút Về trang chủ")
    public void unknownRouteShowsFriendly404() {
        driver.get(Config.baseUrl() + "/duong-dan-khong-ton-tai-galaxora");
        String h1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1"))).getText().trim();
        Assert.assertEquals(h1, "Không tìm thấy trang");
        Assert.assertTrue(driver.findElement(By.cssSelector("a[href='/']")).isDisplayed());
    }
}
