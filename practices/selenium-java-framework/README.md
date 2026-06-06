# Selenium Java Automation Framework

E2E Web UI Automation Framework — **Java 17 · Selenium 4.43.0 · TestNG 7.12.0 · Allure · Log4j2**

---

## 📋 Tech Stack

| Layer | Technology | Version |
|---|---|---|
| Language | Java | 17+ |
| Web Driver | Selenium WebDriver | 4.43.0 |
| Test Runner | TestNG | 7.12.0 |
| Driver Manager | WebDriverManager | 5.9.2 |
| Report | Allure Report | 2.29.1 |
| Logging | Log4j 2 | 2.24.3 |
| JSON | Jackson | 2.18.3 |
| Test Data | JavaFaker | 1.0.2 |
| Build Tool | Maven | 3.8+ |
| CI/CD | GitHub Actions | — |

---

## 🗂️ Project Structure

```
selenium-java-framework/
├── pom.xml                             # Maven dependencies + plugins
├── testng.xml                          # TestNG suite config (smoke + regression)
├── .env                                # Environment config (không commit)
├── .env.example                        # Template cấu hình
├── .gitignore
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/framework/
│   │   │   ├── config/
│   │   │   │   └── ConfigReader.java      # Singleton config manager
│   │   │   ├── drivers/
│   │   │   │   └── DriverFactory.java     # ThreadLocal WebDriver factory
│   │   │   ├── pages/
│   │   │   │   ├── BasePage.java          # Abstract base page (common methods)
│   │   │   │   ├── LoginPage.java         # Login page object
│   │   │   │   └── DashboardPage.java     # Dashboard page object
│   │   │   ├── models/
│   │   │   │   └── UserCredentials.java   # Test data model
│   │   │   ├── listeners/
│   │   │   │   ├── AllureListener.java    # Auto screenshot on failure
│   │   │   │   └── TestListener.java      # Suite/test lifecycle logging
│   │   │   └── utils/
│   │   │       ├── WaitHelper.java        # Smart waits (no Thread.sleep)
│   │   │       ├── ScreenshotUtil.java    # Screenshot + Allure attach
│   │   │       ├── TestDataGenerator.java # Unique traceable test data
│   │   │       └── JsonDataReader.java    # JSON test data reader
│   │   └── resources/
│   │       └── log4j2.xml                 # Logging config (console + file)
│   └── test/
│       ├── java/com/framework/
│       │   ├── base/
│       │   │   └── BaseTest.java          # Abstract base test (setup/teardown)
│       │   └── tests/
│       │       ├── LoginTest.java         # 5 login test cases
│       │       └── DashboardTest.java     # 3 dashboard test cases
│       └── resources/
│           ├── allure.properties          # Allure results directory
│           ├── allure-environment.properties  # Allure environment info
│           └── test-data/
│               └── users.json             # External test data
└── .github/
    └── workflows/
        └── selenium.yml                   # GitHub Actions CI pipeline
```

---

## ⚙️ Prerequisites

| Requirement | Version |
|---|---|
| Java JDK | 17+ |
| Maven | 3.8+ |
| Google Chrome / Firefox / Edge | Latest stable |
| Allure CLI (optional, for local report) | 2.x |

### Cài đặt Allure CLI (optional)

```bash
# macOS (Homebrew)
brew install allure

# Windows (Scoop)
scoop install allure

# Linux
npm install -g allure-commandline
```

---

## 🚀 Quick Start

### 1. Clone và setup

```bash
git clone <repo-url>
cd practices/selenium-java-framework
```

### 2. Cấu hình môi trường

```bash
# Copy template và điền thông tin thực tế
cp .env.example .env
```

Chỉnh sửa file `.env`:

```properties
# URL ứng dụng cần test
BASE_URL=https://your-app.com

# Browser: chrome | firefox | edge
BROWSER=chrome

# Headless mode (false khi debug, true khi CI)
HEADLESS=false

# Credentials
TEST_EMAIL=your-test-email@example.com
TEST_PASSWORD=your-test-password
```

### 3. Build project

```bash
mvn clean compile
```

### 4. Chạy tests

```bash
# Chạy toàn bộ suite (theo testng.xml)
mvn clean test

# Chỉ chạy smoke tests
mvn clean test -Dgroups=smoke

# Chỉ chạy regression tests
mvn clean test -Dgroups=regression

# Chạy với browser cụ thể
mvn clean test -Dbrowser=firefox

# Chạy headless
mvn clean test -Dheadless=true

# Kết hợp nhiều options
mvn clean test -Dbrowser=chrome -Dheadless=true -Dgroups=smoke
```

---

## 📊 Xem Allure Report

```bash
# Sinh và mở report (sau khi mvn test)
mvn allure:serve

# Hoặc sinh report thành file
mvn allure:report
# Report sẽ tạo tại: target/allure-report/index.html
```

---

## 🔍 Logging

Log files được lưu tại:
- `target/logs/SeleniumFramework.log` — Full log
- `target/logs/SeleniumFramework-errors.log` — Chỉ errors

Cấu hình logging trong: `src/main/resources/log4j2.xml`

---

## ✍️ Thêm Test Mới

### 1. Tạo Page Object

```java
// src/main/java/com/framework/pages/MyPage.java
package com.framework.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

public class MyPage extends BasePage {

    // Khai báo locators (inspect DOM thực tế trước)
    private final By myElement = By.id("element-id");

    public MyPage() {
        super();
    }

    @Step("Do something on My Page")
    public MyPage doSomething() {
        click(myElement);
        return this;
    }

    public boolean isOnMyPage() {
        return getCurrentUrl().contains("/my-page");
    }
}
```

### 2. Tạo Test Class

```java
// src/test/java/com/framework/tests/MyTest.java
package com.framework.tests;

import com.framework.base.BaseTest;
import com.framework.pages.MyPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("My Feature")
@Feature("My Module")
public class MyTest extends BaseTest {

    @Test(groups = {"smoke"}, description = "TC_MY_01: Verify something")
    @Story("Happy Path")
    @Severity(SeverityLevel.CRITICAL)
    public void tc01_verifySomething() {
        // Arrange
        MyPage myPage = new MyPage();

        // Act
        myPage.doSomething();

        // Assert
        Assert.assertTrue(
            myPage.isOnMyPage(),
            "Should be on My Page. URL: " + getDriver().getCurrentUrl()
        );
    }
}
```

### 3. Thêm vào testng.xml

```xml
<class name="com.framework.tests.MyTest"/>
```

---

## 🎯 Test Data

### Sinh data động (unique + traceable)

```java
import com.framework.utils.TestDataGenerator;

String email    = TestDataGenerator.generateEmail("register");
// Output: auto_register_1712049200@test.local

String username = TestDataGenerator.generateUsername("admin");
// Output: auto_admin_1712049200

String phone    = TestDataGenerator.generatePhoneVN();
// Output: 0901234567

String password = TestDataGenerator.generateStrongPassword();
// Output: Xk@m3Pq1Rs7!
```

### Đọc data từ JSON (data-driven)

```java
// src/test/resources/test-data/users.json
List<UserCredentials> users = JsonDataReader.readListFromClasspath(
    "test-data/users.json", UserCredentials.class
);
```

---

## ⚡ Smart Waits

Framework **KHÔNG sử dụng** `Thread.sleep()`. Mọi wait thông qua `WaitHelper`:

```java
// Trong Page Object, WaitHelper tự động inject qua BasePage
waitForVisible(locator);        // Chờ element visible
waitForClickable(locator);      // Chờ element clickable
wait.waitForInvisible(locator); // Chờ element biến mất
wait.waitForUrlContains("/dashboard"); // Chờ URL thay đổi
```

---

## 🌐 Multi-Browser

```bash
# Chrome (default)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Microsoft Edge
mvn test -Dbrowser=edge
```

---

## 🔄 CI/CD (GitHub Actions)

File: `.github/workflows/selenium.yml`

**Triggers:**
- Push/PR vào branch `main`, `develop`
- Manual trigger qua GitHub Actions UI

**Jobs:**
1. **Build** — compile project
2. **Test** — chạy tests (headless)
3. **Report** — generate Allure HTML report + publish GitHub Pages

**GitHub Secrets cần thiết:**
| Secret | Mô tả |
|---|---|
| `BASE_URL` | URL ứng dụng cần test |
| `TEST_EMAIL` | Email đăng nhập test |
| `TEST_PASSWORD` | Password đăng nhập test |

---

## 📐 Design Principles

| Nguyên tắc | Mô tả |
|---|---|
| **Page Object Model** | Mỗi page → 1 class, locators khai báo trong class |
| **No Hard Sleep** | Chỉ dùng WebDriverWait / ExpectedConditions |
| **Config over Code** | Mọi config qua `.env`, không hardcode |
| **Fail Fast, Log Rich** | Screenshot on fail, structured logging, clear assertions |
| **Thread-Safe** | ThreadLocal WebDriver cho parallel execution |
| **Unique Test Data** | Timestamp-based data, không dùng data trùng |

---

## 🧹 Coding Conventions

- **Package:** `com.framework.*`
- **Page classes:** `{Name}Page.java` (e.g., `LoginPage.java`)
- **Test classes:** `{Name}Test.java` (e.g., `LoginTest.java`)
- **Test methods:** `tc{N}_{action}_{condition}` (e.g., `tc01_loginSuccess_withValidCredentials`)
- **Locators:** `private final By` — đặt ở đầu class, không inline trong method
- **Step logging:** Mọi action dùng `@Step` annotation + `log.info()`

---

## 🐛 Troubleshooting

| Vấn đề | Giải pháp |
|---|---|
| `WebDriver not initialized` | Đảm bảo `@BeforeMethod` trong BaseTest chạy đúng |
| `Element not found` | Inspect DOM thực tế, cập nhật locator trong Page class |
| `Build failed: aspectj` | Kiểm tra version `aspectjweaver` khớp với `pom.xml` |
| `.env not found` | Copy từ `.env.example`, đặt trong cùng thư mục với `pom.xml` |
| `Allure report trống` | Chạy `mvn test` trước, sau đó `mvn allure:serve` |

---

## 📞 Support

Tham chiếu skill và rules của team:
- `.agent/rules/automation_rules.md` — General best practices
- `.agent/rules/selenium_rules.md` — Selenium-specific rules
- `.agent/rules/locator_strategy.md` — Locator selection priority
