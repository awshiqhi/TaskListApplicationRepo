package com.example.tasklistapp.Controller;

import com.example.tasklistapp.Model.MyAppUser;
import com.example.tasklistapp.Model.MyAppUserRepository;
import com.example.tasklistapp.dto.UserRegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Registration Controller Tests")
class RegistrationControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    @Test
    @DisplayName("Should successfully register user with valid data")
    void testSuccessfulRegistration() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "newuser123",
            "newuser@example.com",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful! You can now log in."));
    }
    
    @Test
    @DisplayName("Should reject registration with weak password")
    void testWeakPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "testuser",
            "test@example.com",
            "p2"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Password must be between 3 and 128 characters")));
    }
    
    @Test
    @DisplayName("Should reject SQL injection attempt in username")
    void testSQLInjectionInUsername() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "admin'--",
            "test@example.com",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should reject SQL injection attempt in password")
    void testSQLInjectionInPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "testuser",
            "test@example.com",
            "Pass123'; DROP TABLE users--"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should reject XSS attempt in username")
    void testXSSInUsername() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "<script>alert(1)</script>",
            "test@example.com",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should reject invalid email format")
    void testInvalidEmail() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "testuser",
            "notanemail",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should reject duplicate username")
    void testDuplicateUsername() throws Exception {
        // Create existing user
        MyAppUser existingUser = new MyAppUser();
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");
        existingUser.setPassword("encodedpassword");
        existingUser.setVerified(true);
        userRepository.save(existingUser);
        
        // Try to register with same username
        UserRegistrationRequest request = new UserRegistrationRequest(
            "existinguser",
            "newemail@example.com",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Registration failed, user already exists"));
    }
    
    @Test
    @DisplayName("Should reject empty username")
    void testEmptyUsername() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "",
            "test@example.com",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should reject empty password")
    void testEmptyPassword() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "testuser",
            "test@example.com",
            ""
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should reject empty email")
    void testEmptyEmail() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "testuser",
            "",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should reject username with invalid characters")
    void testInvalidUsernameCharacters() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "user@#$%",
            "test@example.com",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should accept username with underscores and hyphens")
    void testValidUsernameWithSpecialChars() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
            "test_user-123",
            "testuser@example.com",
            "ValidP@ss123"
        );
        
        mockMvc.perform(post("/req/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
    
    
}
