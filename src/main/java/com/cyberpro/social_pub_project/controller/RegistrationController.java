package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.service.ReCaptchaService;
import com.cyberpro.social_pub_project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final UserService userService;
    private final ReCaptchaService reCaptchaService;

    public RegistrationController(UserService userService, ReCaptchaService reCaptchaService) {
        this.userService = userService;
        this.reCaptchaService = reCaptchaService;
    }
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("g-recaptcha-response") String captchaResponse,  // Add this
                               Model model) {
        try {
            // Validate CAPTCHA first
            if (!reCaptchaService.validateCaptcha(captchaResponse)) {
                model.addAttribute("errorMessage", "Please verify that you are not a robot");
                return "register";
            }

            // Rest of your existing validation...
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                bindingResult.rejectValue("username", "error.user",
                        "Username already exists");
            }

            // Validate existing username
            if (userService.findByUsername(user.getUsername()).isPresent()) {
                bindingResult.rejectValue("username", "error.user",
                        "Username already exists");
            }

            // Validate existing ID
            if (userService.findByIdNumber(user.getIdNumber()).isPresent()) {
                bindingResult.rejectValue("idNumber", "error.user",
                        "ID Number already exists");
            }

            if (bindingResult.hasErrors()) {
                return "register";
            }

            userService.registerUser(user.getUsername(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAge(),
                    user.getIdNumber());

            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "register";
        }
    }
}