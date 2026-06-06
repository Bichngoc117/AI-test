package com.framework.utils;

import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * TestDataGenerator — Sinh test data unique, traceable, random.
 *
 * <p>Mọi field unique (email, username, phone...) đều được gắn timestamp
 * để có thể truy ngược khi test fail.</p>
 *
 * <p>Format chuẩn: {@code prefix_testName_timestamp}</p>
 */
public class TestDataGenerator {

    private static final Logger log = LogManager.getLogger(TestDataGenerator.class);
    private static final Faker faker = new Faker(new Locale("en-US"));

    private TestDataGenerator() {
        // Utility class
    }

    /**
     * Sinh timestamp dạng epoch seconds — dùng làm suffix unique.
     */
    public static long timestamp() {
        return Instant.now().getEpochSecond();
    }

    // ==================== Email ====================

    /**
     * Sinh email unique traceable.
     * Format: auto_{prefix}_{timestamp}@test.local
     */
    public static String generateEmail(String prefix) {
        String sanitized = prefix.toLowerCase().replaceAll("[^a-z0-9]", "_");
        String email = "auto_" + sanitized + "_" + timestamp() + "@test.local";
        log.debug("Generated email: {}", email);
        return email;
    }

    /**
     * Sinh email với default prefix "user".
     */
    public static String generateEmail() {
        return generateEmail("user");
    }

    // ==================== Username ====================

    /**
     * Sinh username unique traceable.
     * Format: auto_{prefix}_{timestamp}
     */
    public static String generateUsername(String prefix) {
        String sanitized = prefix.toLowerCase().replaceAll("[^a-z0-9]", "_");
        String username = "auto_" + sanitized + "_" + timestamp();
        log.debug("Generated username: {}", username);
        return username;
    }

    public static String generateUsername() {
        return generateUsername("user");
    }

    // ==================== Password ====================

    /**
     * Sinh password thỏa mãn các yêu cầu phổ biến:
     * - Ít nhất 8 ký tự
     * - Có chữ hoa, chữ thường, số, ký tự đặc biệt
     */
    public static String generateStrongPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "!@#$%^&*";
        String all = upper + lower + digits + special;

        StringBuilder password = new StringBuilder();
        password.append(upper.charAt(ThreadLocalRandom.current().nextInt(upper.length())));
        password.append(lower.charAt(ThreadLocalRandom.current().nextInt(lower.length())));
        password.append(digits.charAt(ThreadLocalRandom.current().nextInt(digits.length())));
        password.append(special.charAt(ThreadLocalRandom.current().nextInt(special.length())));

        for (int i = 4; i < 12; i++) {
            password.append(all.charAt(ThreadLocalRandom.current().nextInt(all.length())));
        }

        // Shuffle
        for (int i = password.length() - 1; i > 0; i--) {
            int j = ThreadLocalRandom.current().nextInt(i + 1);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }

        return password.toString();
    }

    // ==================== Personal Info ====================

    /**
     * Sinh họ tên đầy đủ ngẫu nhiên.
     */
    public static String generateFullName() {
        return faker.name().fullName();
    }

    /**
     * Sinh first name ngẫu nhiên.
     */
    public static String generateFirstName() {
        return faker.name().firstName();
    }

    /**
     * Sinh last name ngẫu nhiên.
     */
    public static String generateLastName() {
        return faker.name().lastName();
    }

    /**
     * Sinh số điện thoại ngẫu nhiên (Vietnam format).
     * Format: 09xxxxxxxx
     */
    public static String generatePhoneVN() {
        String[] prefixes = {"090", "091", "093", "094", "095", "096", "097", "098", "032", "033", "034", "035", "036", "037", "038", "039", "070", "079", "077", "076", "078"};
        String prefix = prefixes[ThreadLocalRandom.current().nextInt(prefixes.length)];
        StringBuilder phone = new StringBuilder(prefix);
        for (int i = 0; i < 7; i++) {
            phone.append(ThreadLocalRandom.current().nextInt(10));
        }
        return phone.toString();
    }

    /**
     * Sinh số điện thoại quốc tế ngẫu nhiên.
     */
    public static String generatePhone() {
        return faker.phoneNumber().cellPhone();
    }

    // ==================== Address ====================

    public static String generateAddress() {
        return faker.address().streetAddress();
    }

    public static String generateCity() {
        return faker.address().city();
    }

    public static String generateCountry() {
        return faker.address().country();
    }

    public static String generateZipCode() {
        return faker.address().zipCode();
    }

    // ==================== Company ====================

    public static String generateCompanyName() {
        return faker.company().name();
    }

    // ==================== Numeric / Code ====================

    /**
     * Sinh mã code unique traceable.
     * Format: TC_{prefix}_{timestamp}
     */
    public static String generateCode(String prefix) {
        String sanitized = prefix.toUpperCase().replaceAll("[^A-Z0-9]", "_");
        return "TC_" + sanitized + "_" + timestamp();
    }

    /**
     * Sinh số nguyên ngẫu nhiên trong khoảng [min, max].
     */
    public static int randomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * Sinh double ngẫu nhiên trong khoảng [min, max].
     */
    public static double randomDouble(double min, double max) {
        return ThreadLocalRandom.current().nextDouble(min, max);
    }

    // ==================== Text ====================

    public static String generateSentence() {
        return faker.lorem().sentence();
    }

    public static String generateParagraph() {
        return faker.lorem().paragraph();
    }

    /**
     * Sinh chuỗi random chỉ gồm chữ cái.
     */
    public static String randomAlpha(int length) {
        return faker.lorem().characters(length, true, false);
    }

    /**
     * Sinh chuỗi random gồm chữ + số.
     */
    public static String randomAlphanumeric(int length) {
        return faker.lorem().characters(length, true, true);
    }
}
