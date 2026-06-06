package com.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * ForgotPasswordPage — Page Object cho trang khôi phục mật khẩu CRM.
 *
 * <p>URL: https://crm.anhtester.com/admin/authentication/forgot_password</p>
 *
 * <p>Locators đã verify từ DOM thực tế (Perfex CRM v3.1.6).</p>
 */
public class ForgotPasswordPage extends BasePage {

    // ==================== Locators (verified from DOM) ====================

    private final By emailInput     = By.id("email");
    private final By confirmButton  = By.cssSelector("button[type='submit'].btn-primary");
    private final By successAlert   = By.cssSelector(".alert.alert-success");
    private final By errorAlert     = By.cssSelector(".alert.alert-danger");
    private final By pageHeading    = By.cssSelector("h1, h2, .page-title");

    // ==================== Page URL ====================

    public static final String FORGOT_PASSWORD_PATH = "/authentication/forgot_password";

    public ForgotPasswordPage() {
        super();
    }

    // ==================== Navigation ====================

    /**
     * Mở trang Forgot Password trực tiếp.
     */
    @Step("Open forgot password page")
    public ForgotPasswordPage openForgotPasswordPage() {
        String url = config.getBaseUrl() + FORGOT_PASSWORD_PATH;
        log.info("Opening Forgot Password page: {}", url);
        navigateTo(url);
        return this;
    }

    // ==================== Actions ====================

    /**
     * Nhập email vào trường email.
     */
    @Step("Enter email for password recovery: {email}")
    public ForgotPasswordPage enterEmail(String email) {
        log.debug("Entering email: {}", email);
        type(emailInput, email);
        return this;
    }

    /**
     * Click nút Confirm.
     */
    @Step("Click confirm button")
    public ForgotPasswordPage clickConfirm() {
        log.info("Clicking Confirm button");
        click(confirmButton);
        return this;
    }

    /**
     * Thực hiện gửi yêu cầu khôi phục mật khẩu hoàn chỉnh.
     */
    @Step("Submit forgot password with email: {email}")
    public ForgotPasswordPage submitForgotPassword(String email) {
        enterEmail(email);
        clickConfirm();
        return this;
    }

    // ==================== Assertions / State Checks ====================

    /**
     * Kiểm tra đang ở trang Forgot Password không.
     */
    public boolean isOnForgotPasswordPage() {
        return getCurrentUrl().contains("forgot_password");
    }

    /**
     * Kiểm tra alert thành công có hiển thị không.
     */
    public boolean isSuccessAlertDisplayed() {
        return isVisible(successAlert);
    }

    /**
     * Kiểm tra alert lỗi có hiển thị không.
     */
    public boolean isErrorAlertDisplayed() {
        return isVisible(errorAlert);
    }

    /**
     * Lấy text thông báo thành công.
     */
    @Step("Get success alert text")
    public String getSuccessAlertText() {
        return getText(successAlert);
    }

    /**
     * Lấy text thông báo lỗi.
     */
    @Step("Get error alert text")
    public String getErrorAlertText() {
        return getText(errorAlert);
    }

    /**
     * Kiểm tra email input field có hiển thị không.
     */
    public boolean isEmailInputDisplayed() {
        return isVisible(emailInput);
    }

    /**
     * Kiểm tra Confirm button có hiển thị không.
     */
    public boolean isConfirmButtonDisplayed() {
        return isVisible(confirmButton);
    }
}
