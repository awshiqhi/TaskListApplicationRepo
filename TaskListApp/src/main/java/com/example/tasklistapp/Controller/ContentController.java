package com.example.tasklistapp.Controller;

import com.example.tasklistapp.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContentController {
    
    @GetMapping("/req/login")
    public String login(){
        return "login";
    }
    
    @GetMapping("/req/signup")
    public String signup(){
        return "signup";
    }
    
    @GetMapping("/tasklist")
    public String tasklist(Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("You must be logged in to access this page");
        }
        
        String username = authentication.getName();
        model.addAttribute("username", username);
        return "tasklist";
    }
    
}
