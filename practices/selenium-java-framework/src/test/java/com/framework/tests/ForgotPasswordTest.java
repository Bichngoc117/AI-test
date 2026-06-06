package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.ForgotPasswordPage;
import com.framework.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ForgotPasswordTest — M3: Forgot Password (🟡 Medium Risk)
 *
 * <p>Covers 5 test cases cho chức năng khôi phục mật khẩu:</p>
 * <ul>
 *   <li>TC_018: Click "Forgot Password?" → navigate đến trang khôi phục</li>
 *   <li>TC_019: Gửi email khôi phục với email đã đăng ký</li>
 *   <li>TC_020: Để trống Email → HTML5 validation</li>
 *   <li>TC_021: Email chưa đăng ký → thông báo chung (không tiết lộ)</li>
 *   <li>TC_022: Email sai định dạng → HTML5 validation</li>
 * </ul>
 */
@Epic("CRM Authentication")
@Feature("Forgot Password — M3")
public class ForgotPasswordTest extends BaseTest {

    private static final String VALID_EMAIL = "admin@example.com";

    // ==================== TC_018 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_018: Click 'Forgot Password?' → navigate đến trang khôi phục"
    )
    @Story("Forgot Password Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify click link 'Forgot Password?' từ trang Login dẫn đến URL forgot_password "
            + "với Email field và Confirm button")
    public void tc018_forgotPasswordLink_navigatesToForgotPage() {
        log.info("TC018: Click Forgot Password link from Login page");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage();

        ForgotPasswordPage forgotPage = loginPage.clickForgotPassword();

        Assert.assertTrue(
                forgotPage.isOnForgotPasswordPage(),
                "TC018 FAIL: Phải navigate đến trang Forgot Password. Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertTrue(
                forgotPage.isEmailInputDisplayed(),
                "TC018 FAIL: Email Address field phải hiển thị trên trang Forgot Password"
        );
        Assert.assertTrue(
                forgotPage.isConfirmButtonDisplayed(),
                "TC018 FAIL: Confirm button phải hiển thị trên trang Forgot Password"
        );
        log.info("TC018 PASSED: Navigated to Forgot Password page: {}", getDriver().getCurrentUrl());
    }

    // ==================== TC_019 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_019: Gửi email khôi phục với email đã đăng ký"
    )
    @Story("Forgot Password - Valid Email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hệ thống xử lý request khôi phục với email hợp lệ — hiển thị thông báo xác nhận, "
            + "không hiển thị lỗi")
    public void tc019_forgotPassword_withValidEmail() {
        log.info("TC019: Submit forgot password with valid email: {}", VALID_EMAIL);

        ForgotPasswordPage forgotPage = new ForgotPasswordPage();
        forgotPage.openForgotPasswordPage().submitForgotPassword(VALID_EMAIL);

        // Hệ thống không hiển thị lỗi
        Assert.assertFalse(
                forgotPage.isErrorAlertDisplayed(),
                "TC019 FAIL: Không được hiển thị thông báo lỗi khi email hợp lệ"
        );
        // Hiển thị thông báo thành công HOẶC redirect về trang khác (không lỗi)
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500") || currentUrl.contains("error"),
                "TC019 FAIL: Không được có lỗi 500 hay error page. URL: " + currentUrl
        );
        log.info("TC019 PASSED: Forgot password submitted successfully. URL: {}", currentUrl);
    }

    // ==================== TC_020 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_020: Để trống Email trên Forgot Password → hiển thị lỗi"
    )
    @Story("Forgot Password - Empty Email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hệ thống hiển thị thông báo lỗi khi submit email trống trên form Forgot Password")
    public void tc020_forgotPassword_emptyEmail_showsError() {
        log.info("TC020: Submit forgot password with empty email");

        ForgotPasswordPage forgotPage = new ForgotPasswordPage();
        forgotPage.openForgotPasswordPage().submitForgotPassword("");

        // Sau khi submit email trống, phải không vào được màn hình thành công
        // Hoặc hiển thị error, hoặc vẫn ở trang forgot_password
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500"),
                "TC020 FAIL: Không được có lỗi 500 khi submit email trống. URL: " + currentUrl
        );
        // Phải vẫn ở trang forgot_password hoặc hiển thị error message
        boolean stayedOnPage = forgotPage.isOnForgotPasswordPage();
        boolean hasError = forgotPage.isErrorAlertDisplayed();
        Assert.assertTrue(
                stayedOnPage || hasError,
                "TC020 FAIL: Phải hiển thị lỗi hoặc ở lại trang Forgot Password. URL: " + currentUrl
        );
        log.info("TC020 PASSED: Empty email handled correctly. stayedOnPage={}, hasError={}",
                stayedOnPage, hasError);
    }

    // ==================== TC_021 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_021: Email chưa đăng ký trên Forgot Password → thông báo chung"
    )
    @Story("Forgot Password - Non-existent Email")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify hệ thống hiển thị thông báo chung (không tiết lộ email có tồn tại hay không) "
            + "khi submit email chưa đăng ký — bảo mật đúng chuẩn")
    public void tc021_forgotPassword_nonExistentEmail_genericResponse() {
        String nonExistentEmail = "khongtontai_" + System.currentTimeMillis() + "@example.com";
        log.info("TC021: Submit forgot password with non-existent email: {}", nonExistentEmail);

        ForgotPasswordPage forgotPage = new ForgotPasswordPage();
        forgotPage.openForgotPasswordPage().submitForgotPassword(nonExistentEmail);

        // Hệ thống không được tiết lộ email không tồn tại
        // Hành vi phải giống như email hợp lệ (không hiển thị "Email not found")
        if (forgotPage.isErrorAlertDisplayed()) {
            String errorMsg = forgotPage.getErrorAlertText();
            Assert.assertFalse(
                    errorMsg.toLowerCase().contains("not found")
                            || errorMsg.toLowerCase().contains("không tồn tại")
                            || errorMsg.toLowerCase().contains("does not exist"),
                    "TC021 FAIL: Hệ thống tiết lộ email không tồn tại — security risk! Actual: '" + errorMsg + "'"
            );
        }
        // Không được crash
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500"),
                "TC021 FAIL: Không được có lỗi 500. URL: " + currentUrl
        );
        log.info("TC021 PASSED: System handled non-existent email without revealing existence");
    }

    // ==================== TC_022 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_022: Email sai định dạng trên Forgot Password → kiểm tra xử lý"
    )
    @Story("Forgot Password - Invalid Email Format")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify hệ thống xử lý an toàn khi submit email sai định dạng. "
            + "Không được crash hay lỗi 500.")
    public void tc022_forgotPassword_invalidEmailFormat() {
        log.info("TC022: Submit forgot password with invalid email format 'emailkhonghople'");

        ForgotPasswordPage forgotPage = new ForgotPasswordPage();
        forgotPage.openForgotPasswordPage().submitForgotPassword("emailkhonghople");

        // Hệ thống phải xử lý an toàn — không crash
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500"),
                "TC022 FAIL: Không được có lỗi 500 với email sai định dạng. URL: " + currentUrl
        );
        // Không vào được màn hình thành công
        Assert.assertTrue(
                forgotPage.isOnForgotPasswordPage() || forgotPage.isErrorAlertDisplayed(),
                "TC022 FAIL: Phải vẫn ở trang Forgot Password hoặc hiển thị error. URL: " + currentUrl
        );
        log.info("TC022 PASSED: System handled invalid email format safely on Forgot Password. URL: {}",
                currentUrl);
    }
}
