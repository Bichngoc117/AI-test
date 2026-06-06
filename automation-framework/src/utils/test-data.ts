/**
 * Utility for generating unique test data
 */
export class TestDataGenerator {
  /**
   * Generates a random email with a specific prefix
   * @param prefix Prefix for the email (e.g. 'auto_test')
   * @returns unique email string
   */
  static generateRandomEmail(prefix: string = 'test'): string {
    const timestamp = new Date().getTime();
    return `${prefix}_${timestamp}@example.com`;
  }

  /**
   * Generates a random username with a specific prefix
   * @param prefix Prefix for the username
   * @returns unique username string
   */
  static generateRandomUsername(prefix: string = 'user'): string {
    const timestamp = new Date().getTime();
    return `${prefix}_${timestamp}`;
  }
}
