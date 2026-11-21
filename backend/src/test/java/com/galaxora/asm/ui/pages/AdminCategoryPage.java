package com.galaxora.asm.ui.pages;

import java.util.List;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.galaxora.asm.ui.support.Config;

/**
 * Page Object cho màn hình Quản lý Danh mục tại /quan-tri/cua-hang (Y4 - CRUD).
 * Đã rà soát tối ưu theo đợt Review Y6: thu hẹp ngoại lệ DOM, đóng gói kiểm tra hiển thị trên /cua-hang.
 */
public class AdminCategoryPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By tieuDe = By.xpath("//h1[normalize-space()='Quản lý cửa hàng']");
    private final By formTaoMoi = By.xpath("//form[contains(@class,'bh-hang')][.//button[normalize-space()='Tạo danh mục']]");
    private final By tatCaFormDanhMuc = By.xpath("//form[contains(@class,'bh-hang')][.//button[normalize-space()='Lưu danh mục']]");
    private final By oTen = By.cssSelector("input[name='ten']");
    private final By oHienThi = By.cssSelector("input[name='hienThi']");

    public AdminCategoryPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public AdminCategoryPage open() {
        driver.get(Config.baseUrl() + "/quan-tri/cua-hang");
        wait.until(ExpectedConditions.visibilityOfElementLocated(tieuDe));
        wait.until(ExpectedConditions.visibilityOfElementLocated(formTaoMoi));
        return this;
    }

    public void createCategory(String name, boolean visible) {
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(formTaoMoi));
        WebElement input = form.findElement(oTen);
        input.clear();
        input.sendKeys(name);
        WebElement checkbox = form.findElement(oHienThi);
        if (checkbox.isSelected() != visible) {
            checkbox.click();
        }
        form.findElement(By.xpath(".//button[normalize-space()='Tạo danh mục']")).click();
        wait.until(d -> hasCategory(name));
    }

    public boolean tryCreateEmptyAndCheckValidity() {
        WebElement form = wait.until(ExpectedConditions.visibilityOfElementLocated(formTaoMoi));
        WebElement input = form.findElement(oTen);
        input.clear();
        form.findElement(By.xpath(".//button[normalize-space()='Tạo danh mục']")).click();
        return Boolean.TRUE.equals(((JavascriptExecutor) driver)
            .executeScript("return arguments[0].checkValidity();", form));
    }

    public boolean hasCategory(String name) {
        return findRowByName(name) != null;
    }

    public boolean isVisibleInStorefront(String name) {
        driver.get(Config.baseUrl() + "/cua-hang");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("h1")));
        return Boolean.TRUE.equals(wait.until(d -> d.getPageSource().contains(name)));
    }

    public void updateCategory(String oldName, String newName) {
        WebElement form = requireRow(oldName, "sửa");
        WebElement input = form.findElement(oTen);
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
        input.sendKeys(newName);
        form.findElement(By.xpath(".//button[normalize-space()='Lưu danh mục']")).click();
        wait.until(d -> hasCategory(newName) && !hasCategory(oldName));
    }

    public void deleteCategory(String name) {
        WebElement form = requireRow(name, "xóa");
        form.findElement(By.xpath(".//button[normalize-space()='Xóa danh mục']")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        wait.until(d -> !hasCategory(name));
    }

    private WebElement requireRow(String name, String action) {
        WebElement form = findRowByName(name);
        if (form == null) {
            throw new IllegalStateException("Không tìm thấy danh mục cần " + action + ": " + name);
        }
        return form;
    }

    private WebElement findRowByName(String name) {
        List<WebElement> forms = driver.findElements(tatCaFormDanhMuc);
        for (WebElement f : forms) {
            try {
                String val = f.findElement(oTen).getAttribute("value");
                if (name.equals(val)) {
                    return f;
                }
            } catch (StaleElementReferenceException | NoSuchElementException ignored) {
                // Phần tử DOM có thể đang được React cập nhật lại sau gọi API
            }
        }
        return null;
    }
}
