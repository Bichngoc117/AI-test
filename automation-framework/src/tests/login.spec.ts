import { test, expect } from '../fixtures/base.fixture';
import { envConfig } from '../utils/env.config';

test.describe('Login functionality', () => {
  test('should verify framework setup using example.com', async ({ page }) => {
    await test.step('Navigate to example.com', async () => {
      await page.goto('https://example.com');
    });
    
    await test.step('Verify page title and heading', async () => {
      await expect(page).toHaveTitle(/Example Domain/);
      await expect(page.locator('h1')).toHaveText('Example Domain');
    });

    // GHI CHÚ: test thật sẽ gọi loginPage.navigate() và các hàm tương tác
    // await loginPage.navigate();
    // await loginPage.login(envConfig.testEmail, envConfig.testPassword);
  });
});
