package com.galaxora.asm.ui.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Lớp nền cho toàn bộ kịch bản Selenium + TestNG:
 * - Tái sử dụng một phiên trình duyệt cho từng lớp test (@BeforeClass/@AfterClass) để tăng tốc độ chạy.
 * - Xóa sạch cookie trước mỗi test method (@BeforeMethod) để bảo đảm cô lập phiên đăng nhập tuyệt đối.
 * - Tự động chụp ảnh màn hình vào target/screenshots khi có ca FAIL.
 */
public abstract class BaseUiTest {
    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeClass(alwaysRun = true)
    public void openBrowserForClass() {
        driver = DriverFactory.create();
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    @BeforeMethod(alwaysRun = true)
    public void resetSessionBeforeTest() {
        if (driver != null) {
            try {
                if (!driver.getCurrentUrl().startsWith(Config.baseUrl())) {
                    driver.get(Config.baseUrl() + "/he-thong");
                }
                driver.manage().deleteAllCookies();
            } catch (Exception ignored) {
                // Trình duyệt ở trang trống ban đầu chưa có cookie
            }
        }
    }

    @AfterMethod(alwaysRun = true)
    public void captureOnFailure(ITestResult result) {
        if (driver != null && result.getStatus() == ITestResult.FAILURE) {
            captureScreenshot("FAIL-" + result.getTestClass().getRealClass().getSimpleName()
                + "-" + result.getName());
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowserForClass() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    protected Path captureScreenshot(String filePrefix) {
        try {
            Path dir = Path.of("target", "screenshots");
            Files.createDirectories(dir);
            String safeName = filePrefix.replaceAll("[^a-zA-Z0-9._-]", "_") + ".png";
            Path out = dir.resolve(safeName);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(out, png);
            return out;
        } catch (IOException ignored) {
            return null;
        }
    }
}
