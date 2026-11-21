package com.galaxora.asm.ui.support;

/**
 * Cấu hình chạy Selenium + TestNG từ system properties (-D...) hoặc biến môi trường.
 * Không ghi cứng mật khẩu trong mã nguồn.
 */
public final class Config {
    private Config() {}

    public static String baseUrl() {
        String url = prop("base.url", "BASE_URL", "http://localhost:5173");
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static String adminEmail() {
        return prop("admin.email", "DEMO_ADMIN_EMAIL", "admin-ui@galaxora.test");
    }

    public static String adminPassword() {
        return prop("admin.password", "DEMO_ADMIN_PASSWORD", "AdminUiTest@2026");
    }

    public static String userEmail() {
        return prop("user.email", "DEMO_USER_EMAIL", "user-ui@galaxora.test");
    }

    public static String userPassword() {
        return prop("user.password", "DEMO_USER_PASSWORD", "UserUiTest@2026");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(prop("headless", "HEADLESS", "true"));
    }

    public static String browser() {
        return prop("browser", "BROWSER", "chrome").toLowerCase();
    }

    private static String prop(String sysProp, String envVar, String fallback) {
        String v = System.getProperty(sysProp);
        if (v != null && !v.isBlank()) return v.trim();
        v = System.getenv(envVar);
        if (v != null && !v.isBlank()) return v.trim();
        return fallback;
    }
}
