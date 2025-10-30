package com.example.tasklistapp.Controller;

import com.example.tasklistapp.dto.UserRegistrationRequest;
import com.example.tasklistapp.exception.DuplicateResourceException;
import com.example.tasklistapp.exception.InvalidRequestException;
import com.example.tasklistapp.validation.InputSanitizer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.tasklistapp.Model.MyAppUser;
import com.example.tasklistapp.Model.MyAppUserRepository;

@RestController
public class RegistrationController {
    
    @Autowired
    private MyAppUserRepository myAppUserRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private InputSanitizer inputSanitizer;
    
    
    @PostMapping(value = "/req/signup", consumes = "application/json")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserRegistrationRequest request){
        
        // Additional security validation
        if (inputSanitizer.containsSQLInjection(request.getUsername())) {
            throw new InvalidRequestException("Username contains invalid characters or potential security threats");
        }
        
        if (inputSanitizer.containsSQLInjection(request.getEmail())) {
            throw new InvalidRequestException("Email contains invalid characters or potential security threats");
        }
        
        if (inputSanitizer.containsXSS(request.getUsername()) || inputSanitizer.containsXSS(request.getEmail())) {
            throw new InvalidRequestException("Input contains potentially malicious content");
        }
        
        // Sanitize inputs
        String sanitizedUsername = inputSanitizer.sanitize(request.getUsername());
        String sanitizedEmail = inputSanitizer.sanitize(request.getEmail());
        
        // Validate username format
        if (!inputSanitizer.isValidUsername(sanitizedUsername)) {
            throw new InvalidRequestException("Username must be 3-50 characters and contain only letters, numbers, underscores, and hyphens");
        }
        
        // Validate email format
        if (!inputSanitizer.isValidEmail(sanitizedEmail)) {
            throw new InvalidRequestException("Email format is invalid");
        }
        
        // Check for existing user
        MyAppUser existingAppUser = myAppUserRepository.findByUsername(sanitizedUsername).orElse(null);
        
        if(existingAppUser != null){
            throw new DuplicateResourceException("Registration failed, user already exists");
        }
        
        // Create new user with sanitized data
        MyAppUser user = new MyAppUser();
        user.setUsername(sanitizedUsername);
        user.setEmail(sanitizedEmail);
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Password is already validated by @ValidPassword
        user.setVerified(true);
        
        myAppUserRepository.save(user);
        
        return new ResponseEntity<>("Registration successful! You can now log in.", HttpStatus.OK);
    }
    
}
