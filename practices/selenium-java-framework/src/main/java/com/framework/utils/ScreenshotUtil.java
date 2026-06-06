package com.framework.utils;

import io.qameta.allure.Allure;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ScreenshotUtil — Chụp screenshot và đính kèm vào Allure report.
 *
 * <p>Hỗ trợ:
 * <ul>
 *   <li>Lưu file vào target/screenshots/</li>
 *   <li>Đính kèm ảnh PNG vào Allure step</li>
 * </ul>
 * </p>
 */
public class ScreenshotUtil {

    private static final Logger log = LogManager.getLogger(ScreenshotUtil.class);
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");
    private static final String SCREENSHOT_DIR = "target/screenshots";

    private ScreenshotUtil() {
        // Utility class
    }

    /**
     * Chụp screenshot, lưu file và đính kèm vào Allure report.
     *
     * @param driver WebDriver instance
     * @param name   Tên mô tả screenshot (không cần extension)
     * @return đường dẫn file screenshot đã lưu
     */
    public static String captureAndAttach(WebDriver driver, String name) {
        String filePath = capture(driver, name);
        attachToAllure(driver, name);
        return filePath;
    }

    /**
     * Chụp screenshot và lưu ra file.
     *
     * @param driver WebDriver instance
     * @param name   Tên file (không có extension)
     * @return absolute path của file screenshot
     */
    public static String capture(WebDriver driver, String name) {
        if (driver == null) {
            log.warn("Cannot capture screenshot: driver is null");
            return null;
        }

        try {
            String timestamp = LocalDateTime.now().format(TIMESTAMP);
            String sanitizedName = sanitizeFileName(name);
            String fileName = sanitizedName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + File.separator + fileName;

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(filePath);
            FileUtils.forceMkdirParent(destFile);
            FileUtils.copyFile(srcFile, destFile);

            log.info("Screenshot saved: {}", filePath);
            return filePath;

        } catch (IOException e) {
            log.error("Failed to save screenshot '{}': {}", name, e.getMessage());
            return null;
        }
    }

    /**
     * Chụp screenshot và đính kèm trực tiếp vào Allure report (không lưu file).
     *
     * @param driver WebDriver instance
     * @param name   Tên hiển thị trong Allure report
     */
    public static void attachToAllure(WebDriver driver, String name) {
        if (driver == null) {
            log.warn("Cannot attach screenshot to Allure: driver is null");
            return;
        }

        try {
            byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Allure.addAttachment(name, "image/png", new ByteArrayInputStream(screenshotBytes), "png");
            log.debug("Screenshot attached to Allure report: {}", name);
        } catch (Exception e) {
            log.error("Failed to attach screenshot '{}' to Allure: {}", name, e.getMessage());
        }
    }

    /**
     * Sanitize tên file — loại bỏ ký tự đặc biệt.
     */
    private static String sanitizeFileName(String name) {
        if (name == null || name.isBlank()) {
            return "screenshot";
        }
        return name.replaceAll("[^a-zA-Z0-9._-]", "_").replaceAll("_{2,}", "_");
    }
}
