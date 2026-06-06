package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.DashboardPage;
import com.framework.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest — M2: Login Authentication (🔴 High Risk)
 *
 * <p>Covers 13 test cases từ CRM Login Module — AI-RBT:</p>
 * <ul>
 *   <li>TC_005: Login thành công với credentials hợp lệ</li>
 *   <li>TC_006: Login thất bại — Email trống</li>
 *   <li>TC_007: Login thất bại — Password trống</li>
 *   <li>TC_008: Login thất bại — Cả Email + Password trống</li>
 *   <li>TC_009: Login thất bại — Email sai định dạng (HTML5 validation)</li>
 *   <li>TC_010: Login thất bại — Email không tồn tại</li>
 *   <li>TC_011: Login thất bại — Email đúng + Password sai</li>
 *   <li>TC_012: Login thất bại — Email sai + Password đúng</li>
 *   <li>TC_013: Login với Remember me — phiên duy trì</li>
 *   <li>TC_014: Login không Remember me — phiên hết</li>
 *   <li>TC_015: Boundary — Email cực dài 1001 ký tự</li>
 *   <li>TC_016: Boundary — Password cực dài 1001 ký tự</li>
 *   <li>TC_017: Truy cập trang Login khi đã đăng nhập → redirect Dashboard</li>
 * </ul>
 */
@Epic("CRM Authentication")
@Feature("Login — M2: Login Authentication")
public class LoginTest extends BaseTest {

    private static final String VALID_EMAIL    = "admin@example.com";
    private static final String VALID_PASSWORD = "123456";
    private static final String ERROR_MSG_INVALID = "Invalid email or password";
    private static final String ERROR_MSG_EMAIL_REQUIRED  = "The Email Address field is required.";
    private static final String ERROR_MSG_PASS_REQUIRED   = "The Password field is required.";

    // ==================== TC_005 ====================

    @Test(
            groups = {"smoke", "regression", "critical"},
            description = "CRM_LOGIN_TC_005: Đăng nhập thành công với Email và Password hợp lệ"
    )
    @Story("Valid Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify user đăng nhập thành công và được redirect đến Dashboard /admin/")
    public void tc005_loginSuccess_withValidCredentials() {
        log.info("TC005: Login with valid credentials email={}", VALID_EMAIL);

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, VALID_PASSWORD);

        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(
                dashboardPage.isOnDashboard(),
                "TC005 FAIL: Sau khi login thành công, user phải được redirect đến Dashboard. "
                        + "Current URL: " + getDriver().getCurrentUrl()
        );
        log.info("TC005 PASSED: User logged in, at Dashboard URL: {}", getDriver().getCurrentUrl());
    }

    // ==================== TC_006 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_006: Đăng nhập thất bại — Để trống Email"
    )
    @Story("Invalid Login - Empty Fields")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hiển thị lỗi 'The Email Address field is required.' khi Email trống")
    public void tc006_loginFail_emptyEmail() {
        log.info("TC006: Login with empty email");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login("", VALID_PASSWORD);

        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC006 FAIL: User phải vẫn ở trang Login khi Email trống. Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC006 FAIL: Error message phải hiển thị khi Email trống"
        );
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertTrue(
                errorMsg.contains(ERROR_MSG_EMAIL_REQUIRED),
                "TC006 FAIL: Error message phải chứa '" + ERROR_MSG_EMAIL_REQUIRED + "'. Actual: '" + errorMsg + "'"
        );
        log.info("TC006 PASSED: Error message displayed: '{}'", errorMsg);
    }

    // ==================== TC_007 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_007: Đăng nhập thất bại — Để trống Password"
    )
    @Story("Invalid Login - Empty Fields")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hiển thị lỗi 'The Password field is required.' khi Password trống")
    public void tc007_loginFail_emptyPassword() {
        log.info("TC007: Login with empty password");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, "");

        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC007 FAIL: User phải vẫn ở trang Login khi Password trống. Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC007 FAIL: Error message phải hiển thị khi Password trống"
        );
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertTrue(
                errorMsg.contains(ERROR_MSG_PASS_REQUIRED),
                "TC007 FAIL: Error message phải chứa '" + ERROR_MSG_PASS_REQUIRED + "'. Actual: '" + errorMsg + "'"
        );
        log.info("TC007 PASSED: Error message displayed: '{}'", errorMsg);
    }

    // ==================== TC_008 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_008: Đăng nhập thất bại — Để trống cả Email và Password"
    )
    @Story("Invalid Login - Empty Fields")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hiển thị đồng thời 2 thông báo lỗi khi cả Email và Password đều trống")
    public void tc008_loginFail_bothFieldsEmpty() {
        log.info("TC008: Login with both email and password empty");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login("", "");

        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC008 FAIL: User phải vẫn ở trang Login. Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC008 FAIL: Error message phải hiển thị khi cả 2 trường trống"
        );
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertTrue(
                errorMsg.contains(ERROR_MSG_EMAIL_REQUIRED),
                "TC008 FAIL: Error phải chứa thông báo thiếu Email. Actual: '" + errorMsg + "'"
        );
        Assert.assertTrue(
                errorMsg.contains(ERROR_MSG_PASS_REQUIRED),
                "TC008 FAIL: Error phải chứa thông báo thiếu Password. Actual: '" + errorMsg + "'"
        );
        log.info("TC008 PASSED: Both error messages displayed: '{}'", errorMsg);
    }

    // ==================== TC_009 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_009: Đăng nhập thất bại — Email sai định dạng (thiếu @)"
    )
    @Story("Invalid Login - Email Format")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify hệ thống từ chối login khi email không đúng định dạng. "
            + "Lưu ý: Email field là type='text', nên validation là server-side (không phải HTML5).")
    public void tc009_loginFail_invalidEmailFormat() {
        log.info("TC009: Login with invalid email format 'invalidemail'");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login("invalidemail", VALID_PASSWORD);

        // Email field là type="text" → không có HTML5 validation → form sẽ submit lên server
        // Server sẽ xử lý và trả về lỗi hoặc vẫn ở trang login
        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertFalse(
                dashboardPage.isOnDashboard(),
                "TC009 FAIL: User KHÔNG được vào Dashboard với email sai định dạng. "
                        + "Current URL: " + getDriver().getCurrentUrl()
        );

        // Server-side validation phải từ chối — vẫn ở trang login hoặc hiển thị lỗi
        Assert.assertTrue(
                loginPage.isOnLoginPage() || loginPage.isErrorMessageDisplayed(),
                "TC009 FAIL: Phải vẫn ở trang Login hoặc hiển thị error. Current URL: " + getDriver().getCurrentUrl()
        );
        log.info("TC009 PASSED: Login blocked for invalid email format 'invalidemail'. URL: {}",
                getDriver().getCurrentUrl());
    }

    // ==================== TC_010 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_010: Đăng nhập thất bại — Email đúng định dạng nhưng không tồn tại"
    )
    @Story("Invalid Login - Non-existent Email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hiển thị 'Invalid email or password' khi email không tồn tại trong hệ thống")
    public void tc010_loginFail_nonExistentEmail() {
        String nonExistentEmail = "notexist_" + System.currentTimeMillis() + "@example.com";
        log.info("TC010: Login with non-existent email: {}", nonExistentEmail);

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(nonExistentEmail, "WrongPass!456");

        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC010 FAIL: User phải vẫn ở trang Login. Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC010 FAIL: Error message phải hiển thị cho email không tồn tại"
        );
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertTrue(
                errorMsg.contains(ERROR_MSG_INVALID),
                "TC010 FAIL: Error phải là '" + ERROR_MSG_INVALID + "'. Actual: '" + errorMsg + "'"
        );
        log.info("TC010 PASSED: 'Invalid email or password' shown for non-existent email");
    }

    // ==================== TC_011 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_011: Đăng nhập thất bại — Email đúng + Password sai"
    )
    @Story("Invalid Login - Wrong Password")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hiển thị 'Invalid email or password' khi email đúng nhưng password sai")
    public void tc011_loginFail_correctEmailWrongPassword() {
        log.info("TC011: Login with correct email, wrong password");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, "SaiMatKhau!789");

        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC011 FAIL: User phải vẫn ở trang Login. Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC011 FAIL: Error message phải hiển thị khi password sai"
        );
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertTrue(
                errorMsg.contains(ERROR_MSG_INVALID),
                "TC011 FAIL: Error phải là '" + ERROR_MSG_INVALID + "' (không tiết lộ email tồn tại). Actual: '" + errorMsg + "'"
        );
        log.info("TC011 PASSED: Generic error message, not revealing email existence");
    }

    // ==================== TC_012 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_012: Đăng nhập thất bại — Email sai + Password đúng"
    )
    @Story("Invalid Login - Wrong Email")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify hiển thị 'Invalid email or password' khi email sai (không tiết lộ password đúng)")
    public void tc012_loginFail_wrongEmailCorrectPassword() {
        log.info("TC012: Login with wrong email, correct password");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login("wrong_admin@example.com", VALID_PASSWORD);

        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC012 FAIL: User phải vẫn ở trang Login. Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "TC012 FAIL: Error message phải hiển thị"
        );
        String errorMsg = loginPage.getErrorMessage();
        Assert.assertTrue(
                errorMsg.contains(ERROR_MSG_INVALID),
                "TC012 FAIL: Error phải là '" + ERROR_MSG_INVALID + "'. Actual: '" + errorMsg + "'"
        );
        log.info("TC012 PASSED: Generic error, not revealing that password is correct");
    }

    // ==================== TC_013 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_013: Đăng nhập thành công với Remember me được tick"
    )
    @Story("Remember Me")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify checkbox Remember me được tick và login thành công. "
            + "Note: Verify phiên duy trì sau đóng browser là manual test.")
    public void tc013_loginSuccess_withRememberMe() {
        log.info("TC013: Login with Remember me checked");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage();
        loginPage.loginWithRememberMe(VALID_EMAIL, VALID_PASSWORD, true);

        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(
                dashboardPage.isOnDashboard(),
                "TC013 FAIL: Sau khi login với Remember me, phải ở Dashboard. "
                        + "Current URL: " + getDriver().getCurrentUrl()
        );
        log.info("TC013 PASSED: Login with Remember me successful, at Dashboard");
    }

    // ==================== TC_014 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_014: Đăng nhập thành công KHÔNG tick Remember me"
    )
    @Story("Remember Me")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login thành công khi không tick Remember me. "
            + "Note: Verify phiên hết sau đóng browser là manual test.")
    public void tc014_loginSuccess_withoutRememberMe() {
        log.info("TC014: Login without Remember me");

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage();
        // Mặc định checkbox không được tick — verify trạng thái đó
        Assert.assertFalse(
                loginPage.isRememberMeChecked(),
                "TC014 FAIL: Remember me checkbox phải không được tick mặc định"
        );

        loginPage.login(VALID_EMAIL, VALID_PASSWORD);

        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(
                dashboardPage.isOnDashboard(),
                "TC014 FAIL: Sau khi login, phải ở Dashboard. Current URL: " + getDriver().getCurrentUrl()
        );
        log.info("TC014 PASSED: Login without Remember me successful");
    }

    // ==================== TC_015 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_015: Boundary — Nhập Email cực dài 1001 ký tự"
    )
    @Story("Boundary Value")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify hệ thống xử lý an toàn khi nhập Email cực dài 1001 ký tự — không crash, không lỗi 500")
    public void tc015_boundary_emailTooLong() {
        String longEmail = "a".repeat(989) + "@example.com"; // 989 + 12 = 1001 ký tự
        log.info("TC015: Login with 1001-char email (length={})", longEmail.length());

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(longEmail, VALID_PASSWORD);

        // Hệ thống phải xử lý an toàn: không crash
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500") || currentUrl.contains("error"),
                "TC015 FAIL: Hệ thống bị lỗi 500 khi nhập email cực dài. URL: " + currentUrl
        );
        // User phải vẫn ở trang Login (không login thành công với email giả)
        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertFalse(
                dashboardPage.isOnDashboard(),
                "TC015 FAIL: User KHÔNG được vào Dashboard với email 1001 ký tự không hợp lệ"
        );
        log.info("TC015 PASSED: System handled long email safely. URL: {}", currentUrl);
    }

    // ==================== TC_016 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_016: Boundary — Nhập Password cực dài 1001 ký tự"
    )
    @Story("Boundary Value")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify hệ thống xử lý an toàn khi nhập Password cực dài 1001 ký tự — không crash, không lỗi 500")
    public void tc016_boundary_passwordTooLong() {
        String longPassword = "Aa1!" + "x".repeat(997); // 1001 ký tự
        log.info("TC016: Login with 1001-char password (length={})", longPassword.length());

        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, longPassword);

        // Hệ thống phải xử lý an toàn: không crash
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                currentUrl.contains("500") || currentUrl.contains("error"),
                "TC016 FAIL: Hệ thống bị lỗi 500 khi nhập password cực dài. URL: " + currentUrl
        );
        // Không vào được Dashboard với password sai
        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertFalse(
                dashboardPage.isOnDashboard(),
                "TC016 FAIL: User KHÔNG được vào Dashboard với password 1001 ký tự"
        );
        // Phải hiển thị thông báo lỗi hoặc vẫn ở trang Login
        Assert.assertTrue(
                loginPage.isOnLoginPage() || loginPage.isErrorMessageDisplayed(),
                "TC016 FAIL: Phải hiển thị lỗi hoặc ở lại trang Login"
        );
        log.info("TC016 PASSED: System handled long password safely");
    }

    // ==================== TC_017 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_017: Truy cập trang Login khi đã đăng nhập → redirect Dashboard"
    )
    @Story("Session Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify khi user đã đăng nhập và truy cập /admin/authentication, hệ thống redirect về Dashboard")
    public void tc017_redirect_toDashboard_whenAlreadyLoggedIn() {
        log.info("TC017: Verify redirect when already logged in");

        // Step 1: Login thành công
        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, VALID_PASSWORD);

        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(
                dashboardPage.isOnDashboard(),
                "TC017 Setup FAIL: Login phải thành công trước. URL: " + getDriver().getCurrentUrl()
        );

        // Step 2: Cố tình truy cập lại trang Login
        String loginUrl = config.getBaseUrl() + LoginPage.LOGIN_PATH;
        log.info("TC017: Navigating to login URL while logged in: {}", loginUrl);
        getDriver().get(loginUrl);
        waitForPageLoad();

        // Step 3: Verify redirect về Dashboard (không hiển thị form Login)
        Assert.assertTrue(
                dashboardPage.isOnDashboard(),
                "TC017 FAIL: Khi đã đăng nhập, truy cập /authentication phải redirect về Dashboard. "
                        + "Current URL: " + getDriver().getCurrentUrl()
        );
        log.info("TC017 PASSED: Redirected to Dashboard when already logged in. URL: {}", getDriver().getCurrentUrl());
    }

    // ==================== Helper ====================

    private void waitForPageLoad() {
        new com.framework.utils.WaitHelper(getDriver()).waitForPageLoad();
    }
}
