package com.framework.listeners;

import com.framework.drivers.DriverFactory;
import com.framework.utils.ScreenshotUtil;
import io.qameta.allure.Allure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * AllureListener — TestNG listener tích hợp Allure report.
 * Tự động đính kèm screenshot + failure details khi test FAIL.
 */
public class AllureListener implements ITestListener {

    private static final Logger log = LogManager.getLogger(AllureListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        log.info("[ALLURE] Test started: {}.{}",
                result.getTestClass().getName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("[ALLURE] Test PASSED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("[ALLURE] Test FAILED: {} | Error: {}",
                result.getMethod().getMethodName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");

        // Attach screenshot to Allure
        try {
            WebDriver driver = DriverFactory.getDriver();
            ScreenshotUtil.attachToAllure(driver, "Screenshot on Failure - " + result.getMethod().getMethodName());
        } catch (Exception e) {
            log.warn("[ALLURE] Could not capture screenshot on failure: {}", e.getMessage());
        }

        // Attach stacktrace
        if (result.getThrowable() != null) {
            Allure.addAttachment("Stack Trace", result.getThrowable().toString());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("[ALLURE] Test SKIPPED: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        onTestFailure(result);
    }
}
