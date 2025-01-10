package com.cyberpro.social_pub_project.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/access-denied")
    public String showMyaccessdenied(){
        return "access-denied";
    }

    @GetMapping("/confirmation")
    public String showConfirmationPage() {
        return "confirmation"; // Ensure confirmation.html exists in src/main/resources/templates
    }

    @GetMapping("/showMyLoginPage")
    public String showLoginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/confirmation";
        }
        return "login";
    }

    @GetMapping("/home")
    public String showHomePage(){
        return "home";
    }
}

