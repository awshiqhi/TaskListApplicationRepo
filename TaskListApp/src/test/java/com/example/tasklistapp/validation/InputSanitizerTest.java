package com.example.tasklistapp.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Input Sanitizer Tests")
class InputSanitizerTest {
    
    private InputSanitizer sanitizer;
    
    @BeforeEach
    void setUp() {
        sanitizer = new InputSanitizer();
    }
    
    // Username validation tests
    
    @Test
    @DisplayName("Should accept valid username")
    void testValidUsername() {
        assertTrue(sanitizer.isValidUsername("john_doe123"));
        assertTrue(sanitizer.isValidUsername("test-user"));
        assertTrue(sanitizer.isValidUsername("admin"));
        assertTrue(sanitizer.isValidUsername("user_2024"));
    }
    
    @Test
    @DisplayName("Should reject username with SQL injection")
    void testUsernameWithSQLInjection() {
        assertFalse(sanitizer.isValidUsername("admin'--"));
        assertFalse(sanitizer.isValidUsername("user'; DROP TABLE users--"));
        assertFalse(sanitizer.isValidUsername("admin' OR '1'='1"));
        assertFalse(sanitizer.isValidUsername("test;DELETE FROM"));
    }
    
    @Test
    @DisplayName("Should reject username with XSS")
    void testUsernameWithXSS() {
        assertFalse(sanitizer.isValidUsername("<script>alert(1)</script>"));
        assertFalse(sanitizer.isValidUsername("user<iframe>"));
        assertFalse(sanitizer.isValidUsername("admin' onerror='alert(1)'"));
    }
    
    @Test
    @DisplayName("Should reject username that is too short")
    void testUsernameTooShort() {
        assertFalse(sanitizer.isValidUsername("ab"));
        assertFalse(sanitizer.isValidUsername("u"));
    }
    
    @Test
    @DisplayName("Should reject username that is too long")
    void testUsernameTooLong() {
        String longUsername = "a".repeat(51);
        assertFalse(sanitizer.isValidUsername(longUsername));
    }
    
    @Test
    @DisplayName("Should reject username with special characters")
    void testUsernameWithInvalidChars() {
        assertFalse(sanitizer.isValidUsername("user@domain"));
        assertFalse(sanitizer.isValidUsername("test user"));
        assertFalse(sanitizer.isValidUsername("admin!"));
        assertFalse(sanitizer.isValidUsername("user#123"));
    }
    
    @Test
    @DisplayName("Should reject empty or null username")
    void testEmptyOrNullUsername() {
        assertFalse(sanitizer.isValidUsername(""));
        assertFalse(sanitizer.isValidUsername(null));
        assertFalse(sanitizer.isValidUsername("   "));
    }
    
    // Email validation tests
    
    @Test
    @DisplayName("Should accept valid email")
    void testValidEmail() {
        assertTrue(sanitizer.isValidEmail("test@example.com"));
        assertTrue(sanitizer.isValidEmail("user.name@domain.co.uk"));
        assertTrue(sanitizer.isValidEmail("admin+tag@company.org"));
        assertTrue(sanitizer.isValidEmail("test_user@test-domain.com"));
    }
    
    @Test
    @DisplayName("Should reject email with SQL injection")
    void testEmailWithSQLInjection() {
        assertFalse(sanitizer.isValidEmail("test@example.com'; DROP TABLE--"));
        assertFalse(sanitizer.isValidEmail("admin'--@domain.com"));
        assertFalse(sanitizer.isValidEmail("user@domain.com; DELETE"));
    }
    
    @Test
    @DisplayName("Should reject email with XSS")
    void testEmailWithXSS() {
        assertFalse(sanitizer.isValidEmail("<script>@example.com"));
        assertFalse(sanitizer.isValidEmail("test@<iframe>.com"));
        assertFalse(sanitizer.isValidEmail("user@domain.com<script>"));
    }
    
    @Test
    @DisplayName("Should reject invalid email format")
    void testInvalidEmailFormat() {
        assertFalse(sanitizer.isValidEmail("notanemail"));
        assertFalse(sanitizer.isValidEmail("missing@domain"));
        assertFalse(sanitizer.isValidEmail("@domain.com"));
        assertFalse(sanitizer.isValidEmail("user@"));
        assertFalse(sanitizer.isValidEmail("user domain@test.com"));
    }
    
    @Test
    @DisplayName("Should reject empty or null email")
    void testEmptyOrNullEmail() {
        assertFalse(sanitizer.isValidEmail(""));
        assertFalse(sanitizer.isValidEmail(null));
        assertFalse(sanitizer.isValidEmail("   "));
    }
    
    // Sanitization tests
    
    @Test
    @DisplayName("Should sanitize input by removing null bytes")
    void testSanitizeNullBytes() {
        String input = "test\0data";
        String result = sanitizer.sanitize(input);
        assertFalse(result.contains("\0"));
        assertEquals("testdata", result);
    }
    
    @Test
    @DisplayName("Should sanitize input by trimming whitespace")
    void testSanitizeTrimming() {
        assertEquals("test", sanitizer.sanitize("  test  "));
        assertEquals("test data", sanitizer.sanitize("  test data  "));
    }
    
    @Test
    @DisplayName("Should sanitize input by removing control characters")
    void testSanitizeControlChars() {
        String input = "test\u0001\u0002data";
        String result = sanitizer.sanitize(input);
        assertEquals("testdata", result);
    }
    
    @Test
    @DisplayName("Should handle null input in sanitization")
    void testSanitizeNullInput() {
        assertNull(sanitizer.sanitize(null));
    }
    
    @Test
    @DisplayName("Should preserve valid characters in sanitization")
    void testSanitizePreservesValid() {
        String input = "Valid-User_123";
        String result = sanitizer.sanitize(input);
        assertEquals(input, result);
    }
    
    // SQL Injection detection tests
    
    @Test
    @DisplayName("Should detect SQL injection patterns")
    void testDetectSQLInjection() {
        assertTrue(sanitizer.containsSQLInjection("SELECT * FROM users"));
        assertTrue(sanitizer.containsSQLInjection("INSERT INTO"));
        assertTrue(sanitizer.containsSQLInjection("DROP TABLE users"));
        assertTrue(sanitizer.containsSQLInjection("DELETE FROM"));
        assertTrue(sanitizer.containsSQLInjection("admin'--"));
        assertTrue(sanitizer.containsSQLInjection("test; DROP"));
        assertTrue(sanitizer.containsSQLInjection("UNION SELECT"));
        assertTrue(sanitizer.containsSQLInjection("/* comment */"));
        assertTrue(sanitizer.containsSQLInjection("exec sp_"));
    }
    
    @Test
    @DisplayName("Should not flag clean input as SQL injection")
    void testNoFalsePositiveSQLInjection() {
        assertFalse(sanitizer.containsSQLInjection("normal text"));
        assertFalse(sanitizer.containsSQLInjection("user@example.com"));
        assertFalse(sanitizer.containsSQLInjection("test123"));
    }
    
    @Test
    @DisplayName("Should handle null in SQL injection check")
    void testSQLInjectionNullInput() {
        assertFalse(sanitizer.containsSQLInjection(null));
    }
    
    // XSS detection tests
    
    @Test
    @DisplayName("Should detect XSS patterns")
    void testDetectXSS() {
        assertTrue(sanitizer.containsXSS("<script>alert(1)</script>"));
        assertTrue(sanitizer.containsXSS("<iframe src='evil.com'>"));
        assertTrue(sanitizer.containsXSS("<object data='malicious'>"));
        assertTrue(sanitizer.containsXSS("javascript:alert(1)"));
        assertTrue(sanitizer.containsXSS("onerror='alert(1)'"));
        assertTrue(sanitizer.containsXSS("onload='malicious()'"));
        assertTrue(sanitizer.containsXSS("eval('code')"));
    }
    
    @Test
    @DisplayName("Should not flag clean input as XSS")
    void testNoFalsePositiveXSS() {
        assertFalse(sanitizer.containsXSS("normal text"));
        assertFalse(sanitizer.containsXSS("user@example.com"));
        assertFalse(sanitizer.containsXSS("test123"));
        assertFalse(sanitizer.containsXSS("My name is John"));
    }
    
    @Test
    @DisplayName("Should handle null in XSS check")
    void testXSSNullInput() {
        assertFalse(sanitizer.containsXSS(null));
    }
    
    // Case insensitivity tests
    
    @Test
    @DisplayName("Should detect SQL injection regardless of case")
    void testCaseInsensitiveSQLDetection() {
        assertTrue(sanitizer.containsSQLInjection("select"));
        assertTrue(sanitizer.containsSQLInjection("SELECT"));
        assertTrue(sanitizer.containsSQLInjection("SeLeCt"));
        assertTrue(sanitizer.containsSQLInjection("DROP"));
        assertTrue(sanitizer.containsSQLInjection("drop"));
    }
    
    @Test
    @DisplayName("Should detect XSS regardless of case")
    void testCaseInsensitiveXSSDetection() {
        assertTrue(sanitizer.containsXSS("<SCRIPT>"));
        assertTrue(sanitizer.containsXSS("<script>"));
        assertTrue(sanitizer.containsXSS("<ScRiPt>"));
        assertTrue(sanitizer.containsXSS("JAVASCRIPT:"));
        assertTrue(sanitizer.containsXSS("javascript:"));
    }
}
