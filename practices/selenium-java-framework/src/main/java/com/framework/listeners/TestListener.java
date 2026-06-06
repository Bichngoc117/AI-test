package com.framework.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

/**
 * TestListener — Log lifecycle của TestNG suite/test/method.
 */
public class TestListener implements ITestListener, ISuiteListener {

    private static final Logger log = LogManager.getLogger(TestListener.class);

    // ==================== ISuiteListener ====================

    @Override
    public void onStart(ISuite suite) {
        log.info("╔══════════════════════════════════════╗");
        log.info("║   SUITE STARTED: {}  ║", padRight(suite.getName(), 20));
        log.info("╚══════════════════════════════════════╝");
    }

    @Override
    public void onFinish(ISuite suite) {
        log.info("╔══════════════════════════════════════╗");
        log.info("║   SUITE FINISHED: {}  ║", padRight(suite.getName(), 19));
        log.info("╚══════════════════════════════════════╝");
    }

    // ==================== ITestListener ====================

    @Override
    public void onStart(ITestContext context) {
        log.info(">>> Test Context Started: {}", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        int passed = context.getPassedTests().size();
        int failed = context.getFailedTests().size();
        int skipped = context.getSkippedTests().size();
        log.info("<<< Test Context Finished: {} | ✅ PASS: {} | ❌ FAIL: {} | ⏭ SKIP: {}",
                context.getName(), passed, failed, skipped);
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("▶ START: {}.{}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("✅ PASS: {} ({}ms)",
                result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("❌ FAIL: {} ({}ms) | {}",
                result.getMethod().getMethodName(),
                result.getEndMillis() - result.getStartMillis(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⏭ SKIP: {} | Reason: {}",
                result.getMethod().getMethodName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "No reason");
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
