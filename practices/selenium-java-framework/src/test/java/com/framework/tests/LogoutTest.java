package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.DashboardPage;
import com.framework.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LogoutTest — M4: Logout (🟡 Medium Risk)
 *
 * <p>Covers 2 test cases logout:</p>
 * <ul>
 *   <li>TC_023: Đăng xuất từ Dashboard → quay về trang Login</li>
 *   <li>TC_024: Sau logout, nhấn nút Back → không quay lại được Dashboard</li>
 * </ul>
 */
@Epic("CRM Authentication")
@Feature("Logout — M4")
public class LogoutTest extends BaseTest {

    private static final String VALID_EMAIL    = "admin@example.com";
    private static final String VALID_PASSWORD = "123456";

    // ==================== TC_023 ====================

    @Test(
            groups = {"smoke", "regression", "critical"},
            description = "CRM_LOGIN_TC_023: Đăng xuất từ Dashboard → quay về trang Login"
    )
    @Story("Logout")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verify sau khi logout: "
            + "(1) Redirect về /admin/authentication, "
            + "(2) Form Login hiển thị, "
            + "(3) Dashboard không còn hiển thị")
    public void tc023_logout_redirectsToLoginPage() {
        log.info("TC023: Logout from Dashboard");

        // Arrange: Login trước
        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, VALID_PASSWORD);

        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(
                dashboardPage.isOnDashboard(),
                "TC023 Setup FAIL: Login phải thành công. URL: " + getDriver().getCurrentUrl()
        );

        // Act: Logout
        dashboardPage.logout();

        // Assert: Phải redirect về trang Login
        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC023 FAIL: Sau logout phải redirect về trang Login (/admin/authentication). "
                        + "Current URL: " + getDriver().getCurrentUrl()
        );
        Assert.assertFalse(
                dashboardPage.isOnDashboard(),
                "TC023 FAIL: Dashboard KHÔNG được hiển thị sau khi logout"
        );
        log.info("TC023 PASSED: Logout successful, redirected to Login page: {}", getDriver().getCurrentUrl());
    }

    // ==================== TC_024 ====================

    @Test(
            groups = {"regression"},
            description = "CRM_LOGIN_TC_024: Sau logout, nhấn nút Back → không quay lại được Dashboard"
    )
    @Story("Logout - Session Invalidation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify sau logout, khi nhấn Back trên browser, session đã bị hủy hoàn toàn — "
            + "hệ thống redirect về Login, không hiển thị lại Dashboard từ cache")
    public void tc024_afterLogout_backButton_staysOnLogin() {
        log.info("TC024: Verify Back button after logout doesn't restore Dashboard session");

        // Arrange: Login
        LoginPage loginPage = new LoginPage();
        loginPage.openLoginPage().login(VALID_EMAIL, VALID_PASSWORD);

        DashboardPage dashboardPage = new DashboardPage();
        Assert.assertTrue(
                dashboardPage.isOnDashboard(),
                "TC024 Setup FAIL: Login phải thành công. URL: " + getDriver().getCurrentUrl()
        );

        // Act: Logout
        dashboardPage.logout();
        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "TC024 Setup FAIL: Phải ở trang Login sau logout. URL: " + getDriver().getCurrentUrl()
        );

        // Act: Nhấn nút Back của browser
        log.info("TC024: Clicking browser Back button after logout");
        getDriver().navigate().back();
        new com.framework.utils.WaitHelper(getDriver()).waitForPageLoad();

        // Assert: Phải redirect về Login (không vào được Dashboard)
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse(
                dashboardPage.isOnDashboard(),
                "TC024 FAIL: Sau logout + Back button, Dashboard KHÔNG được hiển thị — session phải đã hủy. "
                        + "Current URL: " + currentUrl
        );
        log.info("TC024 PASSED: Back button after logout correctly prevented Dashboard access. URL: {}", currentUrl);
    }
}
