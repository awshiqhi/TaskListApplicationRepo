package com.example.tasklistapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    
    // Password length constraints
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 128;
    
    // Pattern to detect potentially malicious content (XSS attempts)
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile(
        ".*(<script|<iframe|javascript:|onerror|onload).*",
        Pattern.CASE_INSENSITIVE
    );
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password cannot be empty")
                   .addConstraintViolation();
            return false;
        }
        
        // Check length
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password must be between 3 and 128 characters")
                   .addConstraintViolation();
            return false;
        }
        
        // Check for dangerous XSS patterns (defense in depth)
        if (DANGEROUS_PATTERN.matcher(password).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password contains invalid characters")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}
