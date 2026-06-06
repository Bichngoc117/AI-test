package com.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * DashboardPage — Page Object cho trang Dashboard CRM sau khi login thành công.
 *
 * <p>URL: https://crm.anhtester.com/admin</p>
 *
 * <p>Locators dựa trên Perfex CRM v3.1.6 structure.</p>
 */
public class DashboardPage extends BasePage {

    // ==================== Locators ====================

    // Sidebar navigation — element đặc trưng của dashboard (không có ở login)
    private final By sidebarMenu     = By.cssSelector("#sidebar-wrapper, .sidebar, nav#nav-accordion");
    // Header user profile dropdown
    private final By userMenu        = By.cssSelector("li.header-user-profile a.dropdown-toggle, .header-dropdown-profile");
    // Logout link trong dropdown
    private final By logoutButton    = By.cssSelector("li.header-logout a, a[href*='logout']");
    // Dashboard title / breadcrumb
    private final By dashboardTitle  = By.cssSelector(".page-title-area h4, h4.page-title, #dashboard-title");
    // Loading overlay nếu có
    private final By loadingOverlay  = By.cssSelector(".ajax-modal-loading, .processing");
    // Navigation menu (sidebar)
    private final By navigationMenu  = By.cssSelector("#sidebar-wrapper, .sidebar, nav#nav-accordion, #nav-accordion");

    public static final String DASHBOARD_PATH = "/admin";

    public DashboardPage() {
        super();
    }

    // ==================== Navigation ====================

    /**
     * Mở trang Dashboard trực tiếp.
     */
    @Step("Open dashboard page")
    public DashboardPage openDashboard() {
        String url = config.getBaseUrl() + DASHBOARD_PATH;
        log.info("Opening dashboard: {}", url);
        navigateTo(url);
        return this;
    }

    // ==================== Actions ====================

    /**
     * Click vào user menu/avatar để mở dropdown.
     */
    @Step("Click user menu dropdown")
    public DashboardPage clickUserMenu() {
        log.info("Clicking user menu");
        click(userMenu);
        return this;
    }

    /**
     * Thực hiện logout.
     */
    @Step("Logout from CRM application")
    public void logout() {
        log.info("Performing logout");
        clickUserMenu();
        click(logoutButton);
        waitForPageToLoad();
        log.info("Logout completed");
    }

    // ==================== State Checks ====================

    /**
     * Kiểm tra đang ở trang Dashboard (không phải trang authentication).
     *
     * <p>Logic: URL chứa /admin nhưng KHÔNG chứa /authentication</p>
     */
    public boolean isOnDashboard() {
        String currentUrl = getCurrentUrl();
        boolean urlCheck = currentUrl.contains(DASHBOARD_PATH)
                && !currentUrl.contains("/authentication");

        if (urlCheck) {
            return true;
        }
        // Fallback: kiểm tra sidebar menu tồn tại
        return isVisible(sidebarMenu);
    }

    /**
     * Chờ dashboard load hoàn tất.
     */
    @Step("Wait for dashboard to fully load")
    public DashboardPage waitForDashboardLoad() {
        if (isVisible(loadingOverlay)) {
            wait.waitForInvisible(loadingOverlay);
        }
        waitForPageToLoad();
        log.debug("Dashboard fully loaded");
        return this;
    }

    /**
     * Kiểm tra navigation/sidebar menu có hiển thị không.
     */
    public boolean isNavigationMenuVisible() {
        return isVisible(navigationMenu);
    }
}
