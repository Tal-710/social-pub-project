package com.cyberpro.social_pub_project.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class LoginController {

    @GetMapping("/access-denied")
    public String showMyaccessdenied() {
        return "access-denied";
    }

    @GetMapping("/confirmation")
    public String showConfirmationPage() {
        // You could check if confirmation.html exists
        try {
            return "confirmation";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Confirmation page not found");
        }
    }

    @GetMapping("/login")
    public String showMyLoginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/confirmation";
        }
        try {
            return "login";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Login page not found");
        }
    }

    @GetMapping("/home")
    public String showHomePage() {
        try {
            return "home";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Home page not found");
        }
    }
}

