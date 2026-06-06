package com.framework.drivers;

import com.framework.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory — Thread-safe WebDriver factory sử dụng ThreadLocal.
 * Hỗ trợ Chrome, Firefox, Edge với WebDriverManager tự động quản lý binary.
 *
 * <p>Usage:
 * <pre>
 *     DriverFactory.initDriver();
 *     WebDriver driver = DriverFactory.getDriver();
 *     // ... test actions ...
 *     DriverFactory.quitDriver();
 * </pre>
 * </p>
 */
public class DriverFactory {

    private static final Logger log = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverFactory() {
        // Utility class — no instantiation
    }

    /**
     * Khởi tạo WebDriver cho thread hiện tại.
     */
    public static void initDriver() {
        String browser = System.getProperty("browser", config.getBrowser()).toLowerCase().trim();
        boolean headless = Boolean.parseBoolean(
                System.getProperty("headless", String.valueOf(config.isHeadless()))
        );
        int width = config.getBrowserWidth();
        int height = config.getBrowserHeight();

        log.info("Initializing [{}] driver | headless={} | resolution={}x{}", browser, headless, width, height);

        WebDriver driver = switch (browser) {
            case "firefox" -> createFirefoxDriver(headless, width, height);
            case "edge" -> createEdgeDriver(headless, width, height);
            default -> createChromeDriver(headless, width, height);
        };

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));

        if (!headless) {
            driver.manage().window().maximize();
        }

        driverThreadLocal.set(driver);
        log.info("Driver initialized successfully for thread: {}", Thread.currentThread().getName());
    }

    /**
     * Lấy WebDriver instance của thread hiện tại.
     * @throws IllegalStateException nếu driver chưa được khởi tạo
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            throw new IllegalStateException(
                    "WebDriver has not been initialized for thread: " + Thread.currentThread().getName() +
                            ". Call DriverFactory.initDriver() first."
            );
        }
        return driver;
    }

    /**
     * Đóng và cleanup WebDriver của thread hiện tại.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
                log.info("Driver quit successfully for thread: {}", Thread.currentThread().getName());
            } catch (Exception e) {
                log.warn("Error while quitting driver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    // ==================== Browser Factories ====================

    private static WebDriver createChromeDriver(boolean headless, int width, int height) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-gpu",
                "--disable-infobars",
                "--disable-notifications",
                "--disable-popup-blocking",
                "--remote-allow-origins=*"
        );

        if (headless) {
            options.addArguments("--headless=new", "--window-size=" + width + "," + height);
        }

        log.debug("ChromeOptions configured: headless={}", headless);
        return new ChromeDriver(options);
    }

    private static WebDriver createFirefoxDriver(boolean headless, int width, int height) {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage");

        if (headless) {
            options.addArguments("--headless", "--width=" + width, "--height=" + height);
        }

        log.debug("FirefoxOptions configured: headless={}", headless);
        return new FirefoxDriver(options);
    }

    private static WebDriver createEdgeDriver(boolean headless, int width, int height) {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-notifications"
        );

        if (headless) {
            options.addArguments("--headless=new", "--window-size=" + width + "," + height);
        }

        log.debug("EdgeOptions configured: headless={}", headless);
        return new EdgeDriver(options);
    }
}
