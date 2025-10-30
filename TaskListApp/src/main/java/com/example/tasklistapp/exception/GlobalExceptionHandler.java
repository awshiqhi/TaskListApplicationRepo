package com.example.tasklistapp.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.tasklistapp.dto.ErrorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            HttpServletRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResourceException(
            DuplicateResourceException ex, 
            HttpServletRequest request) {
        
        log.error("Duplicate resource: {}", ex.getMessage());
        
        // Return plain text message for better frontend parsing
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex, 
            HttpServletRequest request) {
        
        log.error("Unauthorized access: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequestException(
            InvalidRequestException ex, 
            HttpServletRequest request) {
        
        log.error("Invalid request: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(
            UsernameNotFoundException ex, 
            HttpServletRequest request) {
        
        log.error("Username not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            "User not found: " + ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
/* Belo are unused handlers that serve as safety nets for framework-level and edge-case exceptions. 
While not actively thrown in the code, they provide robust error handling for:
Spring Security authentication/authorization failures
Invalid request parameter types
Unexpected runtime errors  */
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, 
            HttpServletRequest request) {
        
        log.error("Authentication failed: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Unauthorized",
            "Authentication failed: " + ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, 
            HttpServletRequest request) {
        
        log.error("Access denied: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            "Access denied: " + ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        
        log.error("Validation failed: {}", ex.getMessage());
        
        // Collect all validation error messages
        StringBuilder errorMessage = new StringBuilder();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            if (errorMessage.length() > 0) {
                errorMessage.append(", ");
            }
            String message = error.getDefaultMessage();
            errorMessage.append(message);
        });
        
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex, 
            HttpServletRequest request) {
        
        log.error("Type mismatch: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName()),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            HttpServletRequest request) {
        
        log.error("Illegal argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, 
            HttpServletRequest request) {
        
        // Silently ignore favicon.ico requests - browsers request this automatically
        if (request.getRequestURI().contains("/favicon.ico")) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        log.error("Unexpected error occurred: ", ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
