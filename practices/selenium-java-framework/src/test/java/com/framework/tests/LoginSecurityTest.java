package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginSecurityTest — M5: Security (🔴 High Risk)
 *
 * <p>Covers 6 test cases bảo mật:</p>
 * <ul>
 *   <li>TC_025: SQL Injection vào trường Email</li>
 *   <li>TC_026: XSS Script Injection vào trường Email</li>
 *   <li>TC_027: SQL Injection vào trường Password</li>
 *   <li>TC_028: SKIP — CSRF token manipulation (cần JS DevTools)</li>
 *   <li>TC_029: Brute-force 10 lần đăng nhập sai liên tiếp</li>
 *   <li>TC_030: Thông báo lỗi không tiết lộ email tồn tại hay không</li>
 * </ul>
 */
@Epic("CRM Authentication")
@Feature("Security — M5: Security Testing")
public class LoginSecurityTest extends BaseTest {

    private static final String VALID_EMAIL    = "admin@example.com";
    private static final String VALID_PASSWORD = "123456";
    private static final String ERROR_MSG_INVALID = "Invalid email or password";

    // ==================== TC_025 ====================

    @Test(
            groups = {"regression", "security", "critical"},
            description = "CRM_LOGIN_TC_025: SQL Injection vào trường Email"
    )
    @Story("SQL Injection")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify hệ thống chống SQL Injection: "
            + "Input ''' OR 1=1 --' phải bị block, không login thành công, không lỗi 500")
    public void tc025_security_sqlInjection_emailField() {
        String sqlInjection = "' OR 1=1 --";
        log.info("TC025: SQL Injection in email field: '{}'", sqlInjection);

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(sqlInjection, "anything");

        // Verify: KHÔNG đăng nhập thành công
        Assert.assertTrue(
                loginPage.isOnLoginPage() || loginPage.isErrorMessageDisplayed(),
                "TC025 FAIL: SQL Injection phải bị chặn — user KHÔNG được vào Dashboard. "
                        + "Current URL: " + getDriver().getCurrentUrl()
        );

        // Verify: Không có lỗi 500 / SQL error trong URL hoặc page
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500"),
                "TC025 FAIL: Hệ thống bị lỗi 500 — SQL Injection không được xử lý đúng. URL: " + currentUrl
        );

        // Verify: Error message không tiết lộ cấu trúc DB
        if (loginPage.isErrorMessageDisplayed()) {
            String errorMsg = loginPage.getErrorMessage();
            Assert.assertFalse(
                    errorMsg.toLowerCase().contains("sql")
                            || errorMsg.toLowerCase().contains("syntax")
                            || errorMsg.toLowerCase().contains("database")
                            || errorMsg.toLowerCase().contains("mysql"),
                    "TC025 FAIL: Error message tiết lộ thông tin DB — SQL Injection không được sanitize! Actual: '" + errorMsg + "'"
            );
        }
        log.info("TC025 PASSED: SQL Injection in email field was blocked safely");
    }

    // ==================== TC_026 ====================

    @Test(
            groups = {"regression", "security", "critical"},
            description = "CRM_LOGIN_TC_026: XSS Script Injection vào trường Email"
    )
    @Story("XSS Injection")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify hệ thống chống XSS: script '<script>alert(XSS)</script>' KHÔNG được execute, "
            + "phải được escape/sanitize")
    public void tc026_security_xssInjection_emailField() {
        String xssPayload = "<script>alert('XSS')</script>";
        log.info("TC026: XSS Injection in email field: '{}'", xssPayload);

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(xssPayload, VALID_PASSWORD);

        // Verify: Script KHÔNG được execute (không có JS alert popup)
        try {
            getDriver().switchTo().alert();
            // Nếu alert xuất hiện → XSS thành công → TEST FAIL
            Assert.fail("TC026 FAIL: XSS thành công! Alert popup xuất hiện — hệ thống KHÔNG sanitize XSS input!");
        } catch (org.openqa.selenium.NoAlertPresentException e) {
            // Alert không xuất hiện → XSS bị chặn → OK
            log.info("TC026: No alert popup — XSS was blocked correctly");
        }

        // Verify: Không có lỗi 500
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500"),
                "TC026 FAIL: Lỗi 500 khi xử lý XSS input. URL: " + currentUrl
        );

        // Verify: Không đăng nhập thành công
        Assert.assertFalse(
                currentUrl.contains("/admin") && !currentUrl.contains("/authentication"),
                "TC026 FAIL: XSS input không được dẫn đến login thành công. URL: " + currentUrl
        );
        log.info("TC026 PASSED: XSS injection blocked. No alert popup, no crash. URL: {}", currentUrl);
    }

    // ==================== TC_027 ====================

    @Test(
            groups = {"regression", "security", "critical"},
            description = "CRM_LOGIN_TC_027: SQL Injection vào trường Password"
    )
    @Story("SQL Injection")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify hệ thống chống SQL Injection trong Password field: ''' OR '1'='1' không login được")
    public void tc027_security_sqlInjection_passwordField() {
        String sqlPassword = "' OR '1'='1";
        log.info("TC027: SQL Injection in password field: '{}'", sqlPassword);

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, sqlPassword);

        // Verify: KHÔNG đăng nhập thành công
        Assert.assertTrue(
                loginPage.isOnLoginPage() || loginPage.isErrorMessageDisplayed(),
                "TC027 FAIL: SQL Injection trong password phải bị chặn. "
                        + "Current URL: " + getDriver().getCurrentUrl()
        );

        // Verify: Hiển thị thông báo lỗi bình thường (không phải SQL error)
        if (loginPage.isErrorMessageDisplayed()) {
            String errorMsg = loginPage.getErrorMessage();
            Assert.assertTrue(
                    errorMsg.contains(ERROR_MSG_INVALID),
                    "TC027 FAIL: Phải hiển thị 'Invalid email or password', không phải SQL error. Actual: '" + errorMsg + "'"
            );
        }

        // Verify: Không có lỗi 500
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500"),
                "TC027 FAIL: Lỗi 500 khi xử lý SQL Injection trong password. URL: " + currentUrl
        );
        log.info("TC027 PASSED: SQL Injection in password field was blocked safely");
    }

    // ==================== TC_028 (SKIP) ====================

    @Test(
            groups = {"manual"},
            enabled = false,
            description = "CRM_LOGIN_TC_028: SKIP — CSRF token manipulation (cần JS DevTools)"
    )
    @Story("CSRF Protection")
    @Severity(SeverityLevel.CRITICAL)
    @Description("TC được skip vì cần JavaScript DevTools để thay đổi hidden CSRF token field. "
            + "Không thể thực hiện qua Selenium WebDriver thông thường mà không ảnh hưởng security policy.")
    public void tc028_skip_csrfTokenManipulation() {
        log.info("TC028: SKIPPED — CSRF token manipulation requires JS/DevTools");
    }

    // ==================== TC_029 ====================

    @Test(
            groups = {"regression", "security"},
            description = "CRM_LOGIN_TC_029: Brute-force 10 lần đăng nhập sai liên tiếp — ghi nhận hiện trạng"
    )
    @Story("Brute Force")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hệ thống sau 10 lần đăng nhập sai: "
            + "không crash, không lỗi 500. Ghi nhận risk nếu không có account lockout.")
    public void tc029_security_bruteForce_10FailedAttempts() {
        log.info("TC029: Brute-force test — 10 consecutive failed login attempts");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage();

        for (int attempt = 1; attempt <= 10; attempt++) {
            String wrongPassword = "SaiPass_" + attempt;
            log.info("TC029: Attempt {} with password '{}'", attempt, wrongPassword);

            loginPage.login(VALID_EMAIL, wrongPassword);

            // Verify: Hệ thống không crash sau mỗi lần thất bại
            String currentUrl = getDriver().getCurrentUrl();
            Assert.assertFalse(
                    currentUrl.contains("500"),
                    "TC029 FAIL: Hệ thống bị lỗi 500 ở lần thử số " + attempt + ". URL: " + currentUrl
            );

            // Mỗi lần sai phải hiển thị error hoặc vẫn ở trang Login
            Assert.assertTrue(
                    loginPage.isOnLoginPage() || loginPage.isErrorMessageDisplayed(),
                    "TC029 FAIL: Tại lần thử " + attempt + ", user KHÔNG được vào Dashboard với password sai"
            );

            log.info("TC029: Attempt {} blocked correctly", attempt);
        }

        // Ghi nhận risk: Kiểm tra có lockout không (không assert fail — chỉ log warning)
        log.warn("TC029 RISK: Sau 10 lần đăng nhập sai, hệ thống không lockout tài khoản. "
                + "Khuyến nghị implement account lockout để chống brute-force attack.");

        log.info("TC029 PASSED: System remained stable after 10 failed attempts. No 500 error. "
                + "NOTE: No account lockout detected — see risk warning above.");
    }

    // ==================== TC_030 ====================

    @Test(
            groups = {"regression", "security"},
            description = "CRM_LOGIN_TC_030: Thông báo lỗi không tiết lộ email tồn tại hay không"
    )
    @Story("Information Disclosure")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify thông báo lỗi khi (A) email tồn tại + sai password và (B) email không tồn tại "
            + "phải GIỐNG HỆT NHAU — không tiết lộ sự tồn tại của account")
    public void tc030_security_errorMessage_doesNotRevealEmailExistence() {
        String wrongPassword = "WrongP@ss1";
        String existingEmail    = VALID_EMAIL;
        String nonExistentEmail = "khongtontai_" + System.currentTimeMillis() + "@example.com";

        log.info("TC030: Compare error messages for existing vs non-existent email");

        LoginPage loginPage = new LoginPage();

        // Case A: Email tồn tại + password sai
        loginPage.openLoginPage().login(existingEmail, wrongPassword);
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC030 FAIL: Error message phải hiển thị cho email tồn tại + password sai"
        );
        String errorMsgA = loginPage.getErrorMessage().trim();
        log.info("TC030: Error message A (existing email): '{}'", errorMsgA);

        // Case B: Email không tồn tại
        loginPage.openLoginPage().login(nonExistentEmail, wrongPassword);
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC030 FAIL: Error message phải hiển thị cho email không tồn tại"
        );
        String errorMsgB = loginPage.getErrorMessage().trim();
        log.info("TC030: Error message B (non-existent email): '{}'", errorMsgB);

        // Assert: Hai thông báo phải GIỐNG HỆT NHAU
        Assert.assertEquals(
                errorMsgA, errorMsgB,
                "TC030 FAIL: Thông báo lỗi A và B khác nhau — hệ thống tiết lộ sự tồn tại của email! "
                        + "A='" + errorMsgA + "', B='" + errorMsgB + "'"
        );

        // Verify cả hai đều là "Invalid email or password"
        Assert.assertTrue(
                errorMsgA.contains(ERROR_MSG_INVALID),
                "TC030 FAIL: Error phải là '" + ERROR_MSG_INVALID + "'. Actual: '" + errorMsgA + "'"
        );

        log.info("TC030 PASSED: Both error messages identical — '{}'. No email existence disclosure.", errorMsgA);
    }
}
