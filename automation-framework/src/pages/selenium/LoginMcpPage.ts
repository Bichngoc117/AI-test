// src/pages/selenium/LoginMcpPage.ts
import { browser_navigate, browser_snapshot, browser_fill, browser_click } from '@selenium/mcp';
import { Page } from 'selenium-webdriver';

export class LoginMcpPage {
  page: Page;
  // locator refs will be stored after snapshot
  emailRef!: string;
  passwordRef!: string;
  loginRef!: string;

  constructor(page: Page) {
    this.page = page;
  }

  /** Take snapshot and extract stable element refs */
  async initFromSnapshot() {
    const snap = await browser_snapshot(this.page);
    // snap.query returns a reference ID (e.g., "@e1")
    this.emailRef = snap.query('input[type="email"], [placeholder*="email"], [aria-label*="email"]');
    this.passwordRef = snap.query('input[type="password"], [placeholder*="password"], [aria-label*="password"]');
    this.loginRef = snap.query('button:has-text("Login"), button[type="submit"]');
  }

  async login(username: string, password: string) {
    await this.initFromSnapshot();
    console.log('[LoginMcpPage] Fill email');
    await browser_fill(this.page, this.emailRef, username);
    console.log('[LoginMcpPage] Fill password');
    await browser_fill(this.page, this.passwordRef, password);
    console.log('[LoginMcpPage] Click login');
    await browser_click(this.page, this.loginRef);
  }
}
