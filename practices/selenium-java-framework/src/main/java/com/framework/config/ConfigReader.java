package com.framework.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ConfigReader — Singleton config manager.
 * Đọc cấu hình từ .env file và System environment variables.
 * Priority: System env > .env file > default value.
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static volatile ConfigReader instance;
    private final Dotenv dotenv;

    private ConfigReader() {
        dotenv = Dotenv.configure()
                .directory(System.getProperty("user.dir"))
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();
        log.info("ConfigReader initialized — working directory: {}", System.getProperty("user.dir"));
    }

    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    /**
     * Lấy giá trị config theo key.
     * @param key tên config key
     * @return giá trị (String)
     * @throws RuntimeException nếu key không tồn tại
     */
    public String get(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = dotenv.get(key);
        }
        if (value == null || value.isBlank()) {
            throw new RuntimeException("Missing required config key: [" + key + "]. " +
                    "Add it to .env file or set as environment variable.");
        }
        return value.trim();
    }

    /**
     * Lấy giá trị config với fallback default.
     * @param key tên config key
     * @param defaultValue giá trị mặc định nếu không tìm thấy
     * @return giá trị
     */
    public String get(String key, String defaultValue) {
        try {
            return get(key);
        } catch (RuntimeException e) {
            log.debug("Key [{}] not found, using default: {}", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Lấy giá trị boolean.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        return Boolean.parseBoolean(val);
    }

    /**
     * Lấy giá trị integer.
     */
    public int getInt(String key, int defaultValue) {
        String val = get(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            log.warn("Invalid integer for key [{}]: '{}', using default: {}", key, val, defaultValue);
            return defaultValue;
        }
    }

    // =============== Convenience getters ===============

    public String getBaseUrl() {
        return get("BASE_URL", "http://localhost:3000");
    }

    public String getBrowser() {
        return get("BROWSER", "chrome");
    }

    public boolean isHeadless() {
        return getBoolean("HEADLESS", false);
    }

    public int getBrowserWidth() {
        return getInt("BROWSER_WIDTH", 1920);
    }

    public int getBrowserHeight() {
        return getInt("BROWSER_HEIGHT", 1080);
    }

    public int getImplicitWait() {
        return getInt("IMPLICIT_WAIT", 10);
    }

    public int getExplicitWait() {
        return getInt("EXPLICIT_WAIT", 30);
    }

    public int getPageLoadTimeout() {
        return getInt("PAGE_LOAD_TIMEOUT", 60);
    }

    public String getTestEmail() {
        return get("TEST_EMAIL", "test@example.com");
    }

    public String getTestPassword() {
        return get("TEST_PASSWORD", "password");
    }

    public String getScreenshotDir() {
        return get("SCREENSHOT_DIR", "target/screenshots");
    }

    public String getEnv() {
        return get("ENV", "staging");
    }
}
