package com.framework.utils;

import com.framework.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitHelper — Smart wait utilities cho Selenium WebDriver.
 *
 * <p>Chỉ sử dụng explicit waits (WebDriverWait / FluentWait).
 * KHÔNG có Thread.sleep hay hard-coded delays.</p>
 */
public class WaitHelper {

    private static final Logger log = LogManager.getLogger(WaitHelper.class);
    private final WebDriver driver;
    private final WebDriverWait defaultWait;
    private final int explicitTimeout;

    public WaitHelper(WebDriver driver) {
        this.driver = driver;
        this.explicitTimeout = ConfigReader.getInstance().getExplicitWait();
        this.defaultWait = new WebDriverWait(driver, Duration.ofSeconds(explicitTimeout));
    }

    /**
     * Chờ element visible trong DOM và trên viewport.
     */
    public WebElement waitForVisible(By locator) {
        log.debug("Waiting for element to be visible: {}", locator);
        return defaultWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Chờ element visible với custom timeout.
     */
    public WebElement waitForVisible(By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Chờ element clickable (visible + enabled).
     */
    public WebElement waitForClickable(By locator) {
        log.debug("Waiting for element to be clickable: {}", locator);
        return defaultWait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Chờ element clickable với custom timeout.
     */
    public WebElement waitForClickable(By locator, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds))
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Chờ element biến mất khỏi DOM.
     */
    public boolean waitForInvisible(By locator) {
        log.debug("Waiting for element to be invisible: {}", locator);
        return defaultWait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Chờ text xuất hiện trong element.
     */
    public boolean waitForTextToBe(By locator, String expectedText) {
        log.debug("Waiting for text '{}' in element: {}", expectedText, locator);
        return defaultWait.until(ExpectedConditions.textToBe(locator, expectedText));
    }

    /**
     * Chờ text chứa pattern xuất hiện trong element.
     */
    public boolean waitForTextContains(By locator, String partialText) {
        log.debug("Waiting for partial text '{}' in element: {}", partialText, locator);
        return defaultWait.until(ExpectedConditions.textToBePresentInElementLocated(locator, partialText));
    }

    /**
     * Chờ attribute của element có giá trị cụ thể.
     */
    public boolean waitForAttributeToBe(By locator, String attribute, String value) {
        return defaultWait.until(ExpectedConditions.attributeToBe(locator, attribute, value));
    }

    /**
     * Chờ URL chứa pattern.
     */
    public boolean waitForUrlContains(String urlFragment) {
        log.debug("Waiting for URL to contain: {}", urlFragment);
        return defaultWait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Chờ URL match chính xác.
     */
    public boolean waitForUrlToBe(String exactUrl) {
        return defaultWait.until(ExpectedConditions.urlToBe(exactUrl));
    }

    /**
     * Chờ title chứa text.
     */
    public boolean waitForTitleContains(String titleFragment) {
        return defaultWait.until(ExpectedConditions.titleContains(titleFragment));
    }

    /**
     * Chờ số lượng element đạt minimum.
     */
    public boolean waitForElementCount(By locator, int minCount) {
        return defaultWait.until(driver ->
                driver.findElements(locator).size() >= minCount
        );
    }

    /**
     * Chờ alert xuất hiện và trả về Alert object.
     */
    public Alert waitForAlert() {
        return defaultWait.until(ExpectedConditions.alertIsPresent());
    }

    /**
     * Chờ trang load hoàn tất (document.readyState = complete).
     */
    public void waitForPageLoad() {
        defaultWait.until(driver ->
                ((JavascriptExecutor) driver)
                        .executeScript("return document.readyState")
                        .equals("complete")
        );
        log.debug("Page load completed");
    }

    /**
     * Kiểm tra element có visible không (không throw exception).
     */
    public boolean isVisible(By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /**
     * FluentWait với polling interval tùy chỉnh (cho dynamic content).
     */
    public WebElement fluentWait(By locator, int timeoutSeconds, int pollingMillis) {
        FluentWait<WebDriver> fluentWait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        return fluentWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }
}
