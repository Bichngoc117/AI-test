package com.framework.pages;

import com.framework.config.ConfigReader;
import com.framework.drivers.DriverFactory;
import com.framework.utils.ScreenshotUtil;
import com.framework.utils.WaitHelper;
import io.qameta.allure.Step;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * BasePage — Abstract base class cho tất cả Page Object classes.
 *
 * <p>Cung cấp:
 * <ul>
 *   <li>Smart waits thông qua WaitHelper (không dùng Thread.sleep)</li>
 *   <li>Common interaction methods (click, type, select, getText...)</li>
 *   <li>Screenshot on action failure</li>
 *   <li>Allure step logging tự động</li>
 * </ul>
 * </p>
 */
public abstract class BasePage {

    protected final Logger log = LogManager.getLogger(this.getClass());
    protected WebDriver driver;
    protected WaitHelper wait;
    protected Actions actions;
    protected ConfigReader config;

    protected BasePage() {
        this.driver = DriverFactory.getDriver();
        this.wait = new WaitHelper(driver);
        this.actions = new Actions(driver);
        this.config = ConfigReader.getInstance();
        PageFactory.initElements(driver, this);
        log.debug("Page initialized: {}", this.getClass().getSimpleName());
    }

    // ==================== Navigation ====================

    @Step("Navigate to URL: {url}")
    public void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        driver.get(url);
        waitForPageToLoad();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    // ==================== Wait Methods ====================

    /**
     * Chờ element visible trên trang.
     */
    protected WebElement waitForVisible(By locator) {
        return wait.waitForVisible(locator);
    }

    /**
     * Chờ element clickable.
     */
    protected WebElement waitForClickable(By locator) {
        return wait.waitForClickable(locator);
    }

    /**
     * Chờ trang load hoàn tất (document.readyState == complete).
     */
    protected void waitForPageToLoad() {
        wait.waitForPageLoad();
    }

    // ==================== Interaction Methods ====================

    /**
     * Click vào element, chờ clickable trước khi click.
     */
    @Step("Click on element: {locator}")
    protected void click(By locator) {
        log.debug("Clicking on: {}", locator);
        try {
            WebElement element = waitForClickable(locator);
            element.click();
        } catch (ElementClickInterceptedException e) {
            log.warn("Direct click failed, trying JavaScript click for: {}", locator);
            clickByJS(locator);
        }
    }

    /**
     * Click element dùng JavaScript (bypass overlay/z-index issues).
     */
    protected void clickByJS(By locator) {
        WebElement element = waitForVisible(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    /**
     * Nhập văn bản vào field, clear trước khi type.
     */
    @Step("Type '{text}' into field: {locator}")
    protected void type(By locator, String text) {
        log.debug("Typing '{}' into: {}", text, locator);
        WebElement element = waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    /**
     * Nhập văn bản + nhấn Enter.
     */
    protected void typeAndEnter(By locator, String text) {
        type(locator, text);
        waitForVisible(locator).sendKeys(Keys.ENTER);
    }

    /**
     * Lấy text content của element.
     */
    protected String getText(By locator) {
        return waitForVisible(locator).getText().trim();
    }

    /**
     * Lấy attribute của element.
     */
    protected String getAttribute(By locator, String attribute) {
        return waitForVisible(locator).getAttribute(attribute);
    }

    /**
     * Lấy value của input field.
     */
    protected String getValue(By locator) {
        return getAttribute(locator, "value");
    }

    /**
     * Chọn option trong dropdown bằng visible text.
     */
    @Step("Select '{optionText}' from dropdown: {locator}")
    protected void selectByText(By locator, String optionText) {
        log.debug("Selecting '{}' from dropdown: {}", optionText, locator);
        Select select = new Select(waitForVisible(locator));
        select.selectByVisibleText(optionText);
    }

    /**
     * Chọn option trong dropdown bằng value.
     */
    protected void selectByValue(By locator, String value) {
        Select select = new Select(waitForVisible(locator));
        select.selectByValue(value);
    }

    // ==================== State Checks ====================

    /**
     * Kiểm tra element có visible trên trang không.
     */
    protected boolean isVisible(By locator) {
        return wait.isVisible(locator);
    }

    /**
     * Kiểm tra element có enabled không.
     */
    protected boolean isEnabled(By locator) {
        try {
            return waitForVisible(locator).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Kiểm tra text có xuất hiện trên trang không.
     */
    protected boolean isTextPresent(String text) {
        return driver.getPageSource().contains(text);
    }

    /**
     * Lấy danh sách tất cả elements khớp với locator.
     */
    protected List<WebElement> getElements(By locator) {
        return driver.findElements(locator);
    }

    /**
     * Đếm số lượng elements khớp với locator.
     */
    protected int countElements(By locator) {
        return getElements(locator).size();
    }

    // ==================== Advanced Actions ====================

    /**
     * Hover qua element.
     */
    protected void hoverOver(By locator) {
        WebElement element = waitForVisible(locator);
        actions.moveToElement(element).perform();
        log.debug("Hovered over: {}", locator);
    }

    /**
     * Double click element.
     */
    protected void doubleClick(By locator) {
        WebElement element = waitForClickable(locator);
        actions.doubleClick(element).perform();
    }

    /**
     * Scroll element vào viewport.
     */
    protected void scrollIntoView(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    /**
     * Scroll xuống cuối trang.
     */
    protected void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Clear field và set giá trị qua JavaScript (bypass readonly).
     */
    protected void setValueByJS(By locator, String value) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='" + value + "';", element);
    }

    // ==================== Screenshot ====================

    /**
     * Chụp screenshot và đính kèm vào Allure report.
     */
    protected void takeScreenshot(String name) {
        ScreenshotUtil.captureAndAttach(driver, name);
    }
}
