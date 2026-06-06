package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.DashboardPage;
import com.framework.pages.LoginPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * DashboardTest — Test cases cho chức năng Dashboard.
 *
 * <p>Bao gồm:
 * <ul>
 *   <li>TC01: Verify Dashboard hiển thị sau login thành công</li>
 *   <li>TC02: Verify navigation menu hiển thị</li>
 *   <li>TC03: Verify logout thành công</li>
 * </ul>
 * </p>
 */
@Epic("Dashboard")
@Feature("Dashboard Navigation")
public class DashboardTest extends BaseTest {

    /**
     * Helper: Login và trả về DashboardPage instance.
     */
    private DashboardPage loginAndGetDashboard() {
        new LoginPage()
                .openLoginPage()
                .login(config.getTestEmail(), config.getTestPassword());
        return new DashboardPage().waitForDashboardLoad();
    }

    // ==================== Test Cases ====================

    @Test(
            groups = {"smoke", "regression"},
            description = "TC_DASH_01: Dashboard hiển thị đúng sau login"
    )
    @Story("Dashboard Display")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify Dashboard page loads correctly after successful login")
    public void tc01_dashboardDisplayed_afterLogin() {
        // Arrange & Act
        log.info("TC_DASH_01: Login and verify Dashboard loads");
        DashboardPage dashboard = loginAndGetDashboard();

        // Assert
        Assert.assertTrue(
                dashboard.isOnDashboard(),
                "User should be on Dashboard after login. Current URL: " + getDriver().getCurrentUrl()
        );
        log.info("TC_DASH_01 PASSED: Dashboard displayed correctly");
    }

    @Test(
            groups = {"regression"},
            description = "TC_DASH_02: Navigation menu hiển thị trên Dashboard"
    )
    @Story("Dashboard Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the navigation/sidebar menu is visible on Dashboard")
    public void tc02_navigationMenu_isVisible() {
        // Arrange & Act
        log.info("TC_DASH_02: Verify navigation menu visibility");
        DashboardPage dashboard = loginAndGetDashboard();

        // Assert
        Assert.assertTrue(
                dashboard.isNavigationMenuVisible(),
                "Navigation menu should be visible on the Dashboard"
        );
        log.info("TC_DASH_02 PASSED: Navigation menu is visible");
    }

    @Test(
            groups = {"smoke", "regression"},
            description = "TC_DASH_03: Logout thành công từ Dashboard"
    )
    @Story("Logout")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify user can logout successfully and is redirected to Login page")
    public void tc03_logout_success() {
        // Arrange
        log.info("TC_DASH_03: Login then logout");
        DashboardPage dashboard = loginAndGetDashboard();

        // Act
        dashboard.logout();

        // Assert — sau logout phải quay về login page
        LoginPage loginPage = new LoginPage();
        Assert.assertTrue(
                loginPage.isOnLoginPage(),
                "After logout, user should be redirected to Login page. Current URL: " + getDriver().getCurrentUrl()
        );
        log.info("TC_DASH_03 PASSED: Logout successful, redirected to Login page");
    }
}
