package com.example.tasklistapp.Config;

import com.example.tasklistapp.Model.MyAppUser;
import com.example.tasklistapp.Model.MyAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private MyAppUserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Check if default user already exists
        if (userRepository.findByUsername("admin").isEmpty()) {
            MyAppUser defaultUser = new MyAppUser();
            defaultUser.setUsername("admin");
            defaultUser.setEmail("admin@demo.com");
            defaultUser.setPassword(passwordEncoder.encode("admin"));
            defaultUser.setVerified(true); // Pre-verified for easy login
            
            userRepository.save(defaultUser);
        }
    }
}
