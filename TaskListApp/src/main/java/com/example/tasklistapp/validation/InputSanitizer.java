package com.example.tasklistapp.validation;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;

/**
 * Utility class for sanitizing and validating user inputs to prevent
 * SQL injection, XSS, and other security threats
 */
@Component
public class InputSanitizer {
    
    // Patterns for detecting malicious inputs
    private static final Pattern SQL_KEYWORDS = Pattern.compile(
        ".*(select|insert|update|delete|drop|create|alter|exec|execute|union|script|javascript|--|;|/\\*|\\*/|xp_|sp_).*",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern XSS_PATTERN = Pattern.compile(
        ".*(<script|<iframe|<object|<embed|<applet|javascript:|onerror|onload|eval\\(|expression\\().*",
        Pattern.CASE_INSENSITIVE
    );
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_-]{3,50}$"
    );
    
    /**
     * Validates username for security threats
     */
    public boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        
        if (SQL_KEYWORDS.matcher(username).matches() || XSS_PATTERN.matcher(username).matches()) {
            return false;
        }
        
        return USERNAME_PATTERN.matcher(username.trim()).matches();
    }
    
    /**
     * Validates email format and security
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        if (SQL_KEYWORDS.matcher(email).matches() || XSS_PATTERN.matcher(email).matches()) {
            return false;
        }
        
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Sanitizes string input by removing potentially dangerous characters
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        // Remove null bytes
        String sanitized = input.replace("\0", "");
        
        // Remove control characters
        sanitized = sanitized.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "");
        
        return sanitized.trim();
    }
    
    /**
     * Checks if input contains SQL injection attempts
     */
    public boolean containsSQLInjection(String input) {
        return input != null && SQL_KEYWORDS.matcher(input).matches();
    }
    
    /**
     * Checks if input contains XSS attempts
     */
    public boolean containsXSS(String input) {
        return input != null && XSS_PATTERN.matcher(input).matches();
    }
}
