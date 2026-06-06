import { Page, Locator, expect, test } from '@playwright/test';

export abstract class BasePage {
  readonly page: Page;
  abstract readonly url: string;

  constructor(page: Page) {
    this.page = page;
  }

  /**
   * Navigate to the page url
   */
  async navigate() {
    await test.step(`Navigate to ${this.url}`, async () => {
      await this.page.goto(this.url);
    });
  }

  /**
   * Wait for an element to be visible
   */
  async waitForElementVisible(locator: Locator) {
    await test.step(`Wait for element visible [Locator: ${locator}]`, async () => {
      await expect(locator).toBeVisible();
    });
  }
  
  /**
   * Get text content from a locator
   */
  async getText(locator: Locator): Promise<string | null> {
    return await test.step(`Get text from element [Locator: ${locator}]`, async () => {
      await expect(locator).toBeVisible();
      return await locator.textContent();
    });
  }
}
