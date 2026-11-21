package com.galaxora.asm.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.galaxora.asm.ui.support.Config;

/**
 * Page Object cho trang Đăng nhập (/tai-khoan/dang-nhap) và trang Hồ sơ phiên (/tai-khoan/phien).
 */
public class LoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By dinhDanh = By.id("dinh-danh");
    private final By matKhau = By.id("mat-khau");
    private final By nutDangNhap = By.cssSelector("form.tai-khoan-form button[type='submit']");
    private final By thongBaoAlert = By.cssSelector("p.tai-khoan-thong-bao[role='alert']");
    private final By thongBaoStatus = By.cssSelector("p.tai-khoan-thong-bao[role='status']");
    private final By tieuDeTaiKhoan = By.id("tieu-de-tai-khoan");
    private final By nutDangXuat = By.xpath("//button[normalize-space()='Đăng xuất']");

    public LoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public LoginPage open() {
        driver.get(Config.baseUrl() + "/tai-khoan/dang-nhap");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dinhDanh));
        return this;
    }

    public void login(String identifier, String password) {
        WebElement idEl = wait.until(ExpectedConditions.visibilityOfElementLocated(dinhDanh));
        idEl.clear();
        idEl.sendKeys(identifier);
        WebElement passEl = driver.findElement(matKhau);
        passEl.clear();
        passEl.sendKeys(password);
        driver.findElement(nutDangNhap).click();
    }

    public void loginAsAdminAndWait() {
        open();
        login(Config.adminEmail(), Config.adminPassword());
        wait.until(ExpectedConditions.urlContains("/quan-tri"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("aside.qt-ben")));
    }

    public void loginAsUserAndWait() {
        open();
        login(Config.userEmail(), Config.userPassword());
        wait.until(d -> !d.getCurrentUrl().contains("/tai-khoan/dang-nhap"));
    }

    public String waitErrorAlert() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(thongBaoAlert)).getText().trim();
    }

    public String waitStatusNotice() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(thongBaoStatus)).getText().trim();
    }

    public String openProfileHeading() {
        driver.get(Config.baseUrl() + "/tai-khoan/phien");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(tieuDeTaiKhoan)).getText().trim();
    }

    public void logoutFromProfile() {
        driver.get(Config.baseUrl() + "/tai-khoan/phien");
        wait.until(ExpectedConditions.elementToBeClickable(nutDangXuat)).click();
        wait.until(ExpectedConditions.urlContains("/tai-khoan/dang-nhap"));
    }
}
