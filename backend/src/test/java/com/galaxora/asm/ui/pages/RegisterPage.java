package com.galaxora.asm.ui.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.galaxora.asm.ui.support.Config;

/**
 * Page Object cho trang Đăng ký tài khoản (/tai-khoan/dang-ky).
 */
public class RegisterPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By form = By.cssSelector("form.tai-khoan-form");
    private final By hoTen = By.id("ho-ten");
    private final By email = By.id("email");
    private final By quocGia = By.id("quoc-gia");
    private final By soDienThoai = By.id("so-dien-thoai");
    private final By matKhau = By.id("mat-khau");
    private final By xacNhanMatKhau = By.id("xac-nhan-mat-khau");
    private final By nutTao = By.cssSelector("form.tai-khoan-form button[type='submit']");
    private final By thongBaoAlert = By.cssSelector("p.tai-khoan-thong-bao[role='alert']");
    private final By thongBaoStatus = By.cssSelector("p.tai-khoan-thong-bao[role='status']");
    private final By canhBaoKhopMatKhau = By.id("tk-kiem-tra-mat-khau");

    public RegisterPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public RegisterPage open() {
        driver.get(Config.baseUrl() + "/tai-khoan/dang-ky");
        wait.until(ExpectedConditions.visibilityOfElementLocated(hoTen));
        return this;
    }

    public boolean hasAllFields() {
        return driver.findElement(hoTen).isDisplayed()
            && driver.findElement(email).isDisplayed()
            && driver.findElement(quocGia).isDisplayed()
            && driver.findElement(soDienThoai).isDisplayed()
            && driver.findElement(matKhau).isDisplayed()
            && driver.findElement(xacNhanMatKhau).isDisplayed()
            && driver.findElement(nutTao).isDisplayed();
    }

    public String submitButtonText() {
        return driver.findElement(nutTao).getText().trim();
    }

    public RegisterPage fill(String name, String mail, String countryCode, String phone, String pass, String confirm) {
        type(hoTen, name);
        type(email, mail);
        if (countryCode != null && !countryCode.isBlank()) {
            new Select(driver.findElement(quocGia)).selectByValue(countryCode);
        }
        type(soDienThoai, phone);
        type(matKhau, pass);
        type(xacNhanMatKhau, confirm);
        return this;
    }

    public void submit() {
        driver.findElement(nutTao).click();
    }

    public boolean isFormValid() {
        WebElement f = driver.findElement(form);
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript("return arguments[0].checkValidity();", f));
    }

    public String waitStatusNotice() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(thongBaoStatus)).getText().trim();
    }

    public String waitErrorAlert() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(thongBaoAlert)).getText().trim();
    }

    public String inlineConfirmWarning() {
        return driver.findElement(canhBaoKhopMatKhau).getText().trim();
    }

    private void type(By locator, String value) {
        WebElement el = driver.findElement(locator);
        el.clear();
        if (value != null && !value.isEmpty()) {
            el.sendKeys(value);
        }
    }
}
