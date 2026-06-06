import { Page, Locator, test } from '@playwright/test';
import { BasePage } from './base.page';

export class LoginPage extends BasePage {
  readonly url = '/login';

  // REPLACE: Update locators after inspecting actual DOM
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginButton: Locator;
  readonly errorMessage: Locator;

  constructor(page: Page) {
    super(page);
    this.usernameInput = page.getByLabel('Username', { exact: false }).or(page.locator('input[name="username"]'));
    this.passwordInput = page.getByLabel('Password', { exact: false }).or(page.locator('input[name="password"]'));
    this.loginButton = page.getByRole('button', { name: /login/i }).or(page.locator('button[type="submit"]'));
    this.errorMessage = page.locator('.error-message, [role="alert"]');
  }

  /**
   * Perform login action
   */
  async login(username: string, password: string) {
    await test.step(`Login with Username: "${username}" and Password: "***"`, async () => {
      await test.step(`Fill Username field [Locator: ${this.usernameInput}] with value: "${username}"`, async () => {
        await this.usernameInput.fill(username);
      });
      await test.step(`Fill Password field [Locator: ${this.passwordInput}]`, async () => {
        await this.passwordInput.fill(password);
      });
      await test.step(`Click Login button [Locator: ${this.loginButton}]`, async () => {
        await this.loginButton.click();
      });
    });
  }
}
