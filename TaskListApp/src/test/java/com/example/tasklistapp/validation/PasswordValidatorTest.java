package com.example.tasklistapp.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import org.junit.jupiter.api.DisplayName;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Password Validator Tests")
class PasswordValidatorTest {
    
    private Validator validator;
    
    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
    @Test
    @DisplayName("Should accept valid strong password")
    void testValidPassword() {
        TestPassword test = new TestPassword("MyP@ssw0rd123");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertTrue(violations.isEmpty(), "Valid password should not have violations");
    }
    
    @Test
    @DisplayName("Should reject password that is too short")
    void testPasswordTooShort() {
        TestPassword test = new TestPassword("P1");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "Password shorter than 3 chars should be rejected");
        assertTrue(violations.iterator().next().getMessage()
            .contains("3 and 128 characters"));
    }
    
    @Test
    @DisplayName("Should reject empty password")
    void testEmptyPassword() {
        TestPassword test = new TestPassword("");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "Empty password should be rejected");
    }
    
    @Test
    @DisplayName("Should reject null password")
    void testNullPassword() {
        TestPassword test = new TestPassword(null);
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "Null password should be rejected");
    }
    
    @Test
    @DisplayName("Should reject password with SQL injection attempt - SELECT")
    void testSQLInjectionSelect() {
        TestPassword test = new TestPassword("Pass123'SELECT*FROM users--");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "SQL injection attempt should be rejected");
        assertTrue(violations.iterator().next().getMessage()
            .contains("invalid characters"));
    }
    
    @Test
    @DisplayName("Should reject password with SQL injection attempt - DROP")
    void testSQLInjectionDrop() {
        TestPassword test = new TestPassword("Pass123';DROP TABLE users--");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "SQL injection attempt should be rejected");
    }
    
    @Test
    @DisplayName("Should reject password with SQL injection attempt - UNION")
    void testSQLInjectionUnion() {
        TestPassword test = new TestPassword("Pass123'UNION SELECT");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "SQL injection attempt should be rejected");
    }
    
    @Test
    @DisplayName("Should reject password with SQL comment markers")
    void testSQLCommentMarkers() {
        TestPassword test = new TestPassword("Pass123--comment");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "SQL comment markers should be rejected");
    }
    
    @Test
    @DisplayName("Should reject password with script tags")
    void testScriptTags() {
        TestPassword test = new TestPassword("Pass123<script>alert(1)</script>");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertFalse(violations.isEmpty(), "Script tags should be rejected");
    }
    
    @Test
    @DisplayName("Should accept simple valid password")
    void testSimpleValidPassword() {
        TestPassword test = new TestPassword("simplepassword123");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertTrue(violations.isEmpty(), "Simple valid password should be accepted");
    }
    
    @Test
    @DisplayName("Should accept password at minimum length")
    void testMinimumLengthPassword() {
        TestPassword test = new TestPassword("pas");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertTrue(violations.isEmpty(), "3-character password should be valid");
    }
    
    @Test
    @DisplayName("Should accept complex valid password")
    void testComplexValidPassword() {
        TestPassword test = new TestPassword("MyC0mpl3x!P@ssW0rd#2024");
        Set<ConstraintViolation<TestPassword>> violations = validator.validate(test);
        assertTrue(violations.isEmpty(), "Complex valid password should be accepted");
    }
    
    // Test helper class
    static class TestPassword {
        @ValidPassword
        private String password;
        
        public TestPassword(String password) {
            this.password = password;
        }
        
        public String getPassword() {
            return password;
        }
    }
}
