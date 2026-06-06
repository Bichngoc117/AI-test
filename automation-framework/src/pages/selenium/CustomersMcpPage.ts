// src/pages/selenium/CustomersMcpPage.ts
import { browser_navigate, browser_snapshot, browser_click } from '@selenium/mcp';
import { Page } from 'selenium-webdriver';

export class CustomersMcpPage {
  page: Page;
  customersMenuRef!: string;
  tableRowRefs!: string[]; // array of row refs

  constructor(page: Page) {
    this.page = page;
  }

  /** Take snapshot and extract refs for menu and table rows */
  async initFromSnapshot() {
    const snap = await browser_snapshot(this.page);
    this.customersMenuRef = snap.query('a:has-text("Customers"), [role="link"][href*="customers"]');
    // Grab all rows in the customers table
    const rows = snap.queryAll('.table tbody tr, table tbody tr');
    this.tableRowRefs = rows; // each element is a stable ref like "@e12"
  }

  async goToCustomers() {
    await this.initFromSnapshot();
    console.log('[CustomersMcpPage] Click Customers menu');
    await browser_click(this.page, this.customersMenuRef);
    // Wait for navigation to finish
    await this.page.wait({ timeout: 10000 }); // generic wait, MCP will handle load state
  }

  async verifyAtLeastOneRow() {
    await this.initFromSnapshot();
    const rowCount = this.tableRowRefs.length;
    console.log(`[CustomersMcpPage] Row count = ${rowCount}`);
    if (rowCount < 1) {
      throw new Error('Customers table has no rows');
    }
  }
}
