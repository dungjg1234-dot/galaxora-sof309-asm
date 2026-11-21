package com.galaxora.asm.ui.support;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

/**
 * Khởi tạo WebDriver (Chrome/Edge) với Selenium Manager tích hợp sẵn từ Selenium 4.6+.
 */
public final class DriverFactory {
    private DriverFactory() {}

    public static WebDriver create() {
        WebDriver driver;
        if ("edge".equals(Config.browser())) {
            EdgeOptions opts = new EdgeOptions();
            if (Config.headless()) opts.addArguments("--headless=new");
            opts.addArguments("--window-size=1366,768", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
            driver = new EdgeDriver(opts);
        } else {
            ChromeOptions opts = new ChromeOptions();
            if (Config.headless()) opts.addArguments("--headless=new");
            opts.addArguments("--window-size=1366,768", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage",
                "--lang=vi-VN");
            driver = new ChromeDriver(opts);
        }
        driver.manage().window().setSize(new Dimension(1366, 768));
        return driver;
    }
}
