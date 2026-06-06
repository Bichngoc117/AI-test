# Playwright + TypeScript Automation Framework

Đây là bộ khung (framework) Automation Testing cho E2E Web UI sử dụng Playwright và TypeScript.

## Yêu cầu hệ thống
- Node.js (phiên bản 18 trở lên)

## Cài đặt

1. Cài đặt các thư viện phụ thuộc:
   ```bash
   npm install
   ```
   *(Sử dụng lệnh `npm ci` khi chạy trên môi trường CI)*

2. Cài đặt trình duyệt Playwright (nếu chưa có):
   ```bash
   npx playwright install --with-deps
   ```

3. Cấu hình môi trường:
   - Copy file `.env.example` thành `.env`
   - Cập nhật các thông tin URL và credentials (như `BASE_URL`, `TEST_EMAIL`, `TEST_PASSWORD`) tương ứng trong `.env`

## Chạy test

- Chạy toàn bộ test ẩn giao diện (headless):
  ```bash
  npm test
  ```
- Chạy test có giao diện (headed):
  ```bash
  npm run test:headed
  ```
- Mở Playwright UI Mode:
  ```bash
  npm run test:ui
  ```

## Xem Báo Cáo (Reports)

Framework hỗ trợ cả báo cáo mặc định của Playwright và Allure Report.

- **Xem Playwright HTML Report:**
  ```bash
  npm run report
  ```

- **Tạo và xem Allure Report (Local):**
  Lưu ý: Bạn cần có Java cài đặt sẵn trong máy để khởi chạy Allure.
  ```bash
  # Tùy chọn: Dọn dẹp report cũ
  npm run report:allure:clean || true
  
  # Sinh report mới từ kết quả test
  npx allure generate allure-results -o allure-report --clean
  
  # Khởi chạy web server để xem report
  npx allure open allure-report
  ```

## Tích hợp CI/CD (GitHub Actions)

Dự án đã được tích hợp sẵn luồng CI tự động thông qua GitHub Actions (`.github/workflows/playwright.yml`).

Quy trình tự động thực hiện các bước sau:
1. Checkout mã nguồn và setup môi trường Node.js.
2. Cài đặt dependencies và Playwright Browsers.
3. Chạy Playwright test sử dụng các **GitHub Secrets** (`BASE_URL`, `TEST_EMAIL`, `TEST_PASSWORD`).
4. **Lưu trữ Artifacts**: Lưu trữ `playwright-report` và `allure-report` vào mục Artifacts trên GitHub (lưu trữ 30 ngày) cho việc debug.
5. **Publish GitHub Pages**: Tự động sinh Allure Report và deploy thẳng lên **GitHub Pages** (nhánh `gh-pages`). Cho phép cả team xem kết quả automation dễ dàng thông qua URL web.

## Cấu trúc thư mục

- `src/pages/`: Chứa các Page Object Models đại diện cho các màn hình/trang của ứng dụng.
- `src/tests/`: Chứa các kịch bản test (Test specs).
- `src/fixtures/`: Các fixture tùy chỉnh để tái sử dụng trạng thái test.
- `src/utils/`: Các tiện ích hỗ trợ (đọc biến môi trường, sinh dữ liệu test,...).
- `.github/workflows/`: Chứa file định nghĩa kịch bản chạy CI/CD trên Github.
