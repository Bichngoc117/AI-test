import { test as base } from '@playwright/test';
import { LoginPage } from '../pages/login.page';

type MyFixtures = {
  loginPage: LoginPage;
  autoScreenshotOnPass: void;
};

export const test = base.extend<MyFixtures>({
  autoScreenshotOnPass: [async ({ page }, use, testInfo) => {
    await use();
    if (testInfo.status === 'passed') {
      const screenshot = await page.screenshot();
      await testInfo.attach('Screenshot on Pass', { body: screenshot, contentType: 'image/png' });
    }
  }, { auto: true }],
  loginPage: async ({ page }, use) => {
    // Setup
    const loginPage = new LoginPage(page);
    
    // Use the fixture value in the test
    await use(loginPage);
    
    // Teardown (if needed)
  },
});

export { expect } from '@playwright/test';
