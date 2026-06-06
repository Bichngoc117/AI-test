package com.framework.base;

import com.framework.config.ConfigReader;
import com.framework.drivers.DriverFactory;
import com.framework.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest — Abstract base class cho tất cả Test classes.
 *
 * <p>Cung cấp:
 * <ul>
 *   <li>Setup / Teardown tự động qua TestNG annotations</li>
 *   <li>Thread-safe WebDriver via DriverFactory</li>
 *   <li>Auto screenshot + Allure attachment khi test FAIL</li>
 *   <li>Logging test lifecycle</li>
 * </ul>
 * </p>
 */
public abstract class BaseTest {

    protected final Logger log = LogManager.getLogger(this.getClass());
    protected ConfigReader config;

    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }

    /**
     * Chạy 1 lần trước toàn bộ suite — setup chung nếu cần.
     */
    @BeforeSuite(alwaysRun = true)
    public void globalSetUp() {
        log.info("========== TEST SUITE STARTED ==========");
        log.info("Environment: {} | Base URL: {}",
                ConfigReader.getInstance().getEnv(),
                ConfigReader.getInstance().getBaseUrl());
    }

    /**
     * Chạy trước mỗi test method — khởi tạo driver.
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional String browser) {
        config = ConfigReader.getInstance();

        // Override browser nếu được pass qua TestNG parameter
        if (browser != null && !browser.isBlank()) {
            System.setProperty("browser", browser);
        }

        log.info("------ Setting up test: {} ------", getClass().getSimpleName());
        DriverFactory.initDriver();

        // Navigate đến base URL
        String baseUrl = config.getBaseUrl();
        if (baseUrl != null && !baseUrl.isBlank() && !baseUrl.equals("https://your-app-url.com")) {
            log.info("Navigating to base URL: {}", baseUrl);
            getDriver().get(baseUrl);
        }
    }

    /**
     * Chạy sau mỗi test method — cleanup driver và chụp screenshot nếu FAIL.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("TEST FAILED: {} | Cause: {}", testName,
                    result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");

            // Auto screenshot on failure
            try {
                WebDriver currentDriver = getDriver();
                if (currentDriver != null) {
                    String screenshotName = "FAIL_" + testName;
                    ScreenshotUtil.captureAndAttach(currentDriver, screenshotName);
                    log.info("Failure screenshot captured: {}", screenshotName);
                }
            } catch (Exception e) {
                log.warn("Could not capture failure screenshot: {}", e.getMessage());
            }

            // Attach failure details to Allure
            if (result.getThrowable() != null) {
                Allure.addAttachment("Failure Details", result.getThrowable().toString());
            }

        } else if (result.getStatus() == ITestResult.SUCCESS) {
            log.info("TEST PASSED: {}", testName);
        } else if (result.getStatus() == ITestResult.SKIP) {
            log.warn("TEST SKIPPED: {}", testName);
        }

        // Quit driver
        DriverFactory.quitDriver();
        log.info("------ Test teardown complete: {} ------", testName);
    }

    /**
     * Chạy 1 lần sau toàn bộ suite.
     */
    @AfterSuite(alwaysRun = true)
    public void globalTearDown() {
        log.info("========== TEST SUITE COMPLETED ==========");
    }

    // ==================== Convenience Methods ====================

    /**
     * Chụp screenshot mid-test và đính kèm vào Allure.
     */
    protected void captureScreenshot(String name) {
        try {
            WebDriver currentDriver = getDriver();
            if (currentDriver != null) {
                ScreenshotUtil.captureAndAttach(currentDriver, name);
            }
        } catch (Exception e) {
            log.warn("Could not capture manual screenshot: {}", e.getMessage());
        }
    }

    /**
     * Đính kèm thông tin vào Allure report.
     */
    protected void attachInfo(String name, String content) {
        Allure.addAttachment(name, content);
    }

    /**
     * Lấy base URL từ config.
     */
    protected String getBaseUrl() {
        return config.getBaseUrl();
    }
}
