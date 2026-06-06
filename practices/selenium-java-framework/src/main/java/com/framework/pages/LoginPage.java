package com.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * LoginPage — Page Object cho trang đăng nhập CRM.
 *
 * <p>URL thực tế: https://crm.anhtester.com/authentication/login</p>
 *
 * <p>Locators đã verify từ DOM thực tế — Perfex CRM v3.1.6 (URL mới).</p>
 *
 * <p>Lưu ý: Email field có type="text" (không phải type="email"),
 * nên HTML5 email validation KHÔNG hoạt động — form sẽ submit lên server.</p>
 */
public class LoginPage extends BasePage {

    // ==================== Locators (verified from DOM — /authentication/login) ====================

    private final By emailInput       = By.id("email");
    private final By passwordInput    = By.id("password");
    private final By loginButton      = By.cssSelector("button[type='submit'].btn-primary");
    private final By rememberCheckbox = By.id("remember");
    private final By forgotPassword   = By.cssSelector("a[href*='forgot_password']");
    private final By errorAlert       = By.cssSelector(".alert.alert-danger");
    private final By passwordField    = By.id("password");

    // ==================== Page URL ====================

    public static final String LOGIN_PATH = "/authentication/login";

    public LoginPage() {
        super();
    }

    // ==================== Navigation ====================

    /**
     * Mở trang login trực tiếp.
     */
    @Step("Open login page")
    public LoginPage openLoginPage() {
        String loginUrl = config.getBaseUrl() + LOGIN_PATH;
        log.info("Opening login page: {}", loginUrl);
        navigateTo(loginUrl);
        return this;
    }

    // ==================== Actions ====================

    /**
     * Nhập email vào field.
     */
    @Step("Enter email: {email}")
    public LoginPage enterEmail(String email) {
        log.debug("Entering email: {}", email);
        type(emailInput, email);
        return this;
    }

    /**
     * Nhập password vào field.
     */
    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        log.debug("Entering password");
        type(passwordInput, password);
        return this;
    }

    /**
     * Click nút Login.
     */
    @Step("Click login button")
    public LoginPage clickLoginButton() {
        log.info("Clicking login button");
        click(loginButton);
        return this;
    }

    /**
     * Tick hoặc bỏ tick Remember me checkbox.
     *
     * @param shouldCheck true để tick, false để bỏ tick
     */
    @Step("Set Remember me: {shouldCheck}")
    public LoginPage setRememberMe(boolean shouldCheck) {
        WebElement checkbox = waitForVisible(rememberCheckbox);
        boolean isChecked = checkbox.isSelected();
        if (shouldCheck && !isChecked) {
            checkbox.click();
            log.info("Checked 'Remember me' checkbox");
        } else if (!shouldCheck && isChecked) {
            checkbox.click();
            log.info("Unchecked 'Remember me' checkbox");
        }
        return this;
    }

    /**
     * Thực hiện login hoàn chỉnh: nhập credentials + click submit.
     */
    @Step("Login with email: {email}")
    public LoginPage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLoginButton();
        return this;
    }

    /**
     * Thực hiện login với tùy chọn Remember me.
     */
    @Step("Login with email: {email}, Remember me: {rememberMe}")
    public LoginPage loginWithRememberMe(String email, String password, boolean rememberMe) {
        enterEmail(email);
        enterPassword(password);
        setRememberMe(rememberMe);
        clickLoginButton();
        return this;
    }

    /**
     * Click link "Forgot Password?".
     */
    @Step("Click forgot password link")
    public ForgotPasswordPage clickForgotPassword() {
        log.info("Clicking Forgot Password link");
        click(forgotPassword);
        return new ForgotPasswordPage();
    }

    // ==================== Assertions / State Checks ====================

    /**
     * Lấy text error message hiển thị trên form.
     */
    @Step("Get error message text")
    public String getErrorMessage() {
        return getText(errorAlert);
    }

    /**
     * Kiểm tra error alert có hiển thị không.
     */
    public boolean isErrorMessageDisplayed() {
        return isVisible(errorAlert);
    }

    /**
     * Kiểm tra đang ở trang login hay không.
     */
    public boolean isOnLoginPage() {
        String url = getCurrentUrl();
        return url.contains("/authentication/login")
                || url.contains("/authentication")
                && !url.contains("forgot_password");
    }

    /**
     * Kiểm tra password field có type="password" (masked) không.
     *
     * <p>Lưu ý: Email field ở trang này là type="text".</p>
     */
    public boolean isPasswordMasked() {
        return "password".equals(getAttribute(passwordField, "type"));
    }

    /**
     * Kiểm tra Remember me checkbox có được tick không.
     */
    public boolean isRememberMeChecked() {
        return waitForVisible(rememberCheckbox).isSelected();
    }

    /**
     * Kiểm tra Forgot Password link có hiển thị không.
     */
    public boolean isForgotPasswordLinkDisplayed() {
        return isVisible(forgotPassword);
    }

    /**
     * Kiểm tra login button có enabled không.
     */
    public boolean isLoginButtonEnabled() {
        return isEnabled(loginButton);
    }

    /**
     * Lấy page title.
     */
    public String getTitle() {
        return getPageTitle();
    }
}
