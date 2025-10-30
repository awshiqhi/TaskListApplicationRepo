package com.example.tasklistapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    
    // Patterns to detect common SQL injection attempts
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        ".*('|(--)|;|/\\*|\\*/|xp_|sp_|exec|execute|select|insert|update|delete|drop|create|alter|union|script|javascript|<|>).*",
        Pattern.CASE_INSENSITIVE
    );
    
    // Password length constraints
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 128;
    
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password cannot be empty")
                   .addConstraintViolation();
            return false;
        }
        
        // Check for SQL injection patterns
        if (SQL_INJECTION_PATTERN.matcher(password).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password contains invalid characters")
                   .addConstraintViolation();
            return false;
        }
        
        // Check length only
        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                "Password must be between 3 and 128 characters")
                   .addConstraintViolation();
            return false;
        } 
        
        return true;
    }
}
