package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.User;
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

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping
    public String registerUser(@Valid @RequestBody @ModelAttribute("user") User user, BindingResult bindingResult, Model model)
    {
        if (userService.findByUsername(user.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.user",
                    "Username already exists");
        }

        if (userService.findByIdNumber(user.getIdNumber()).isPresent()) {
            bindingResult.rejectValue("idNumber", "error.user",
                    "ID Number already exists");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(user.getUsername(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAge(),
                    user.getIdNumber());
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Registration failed");
            return "register";
        }
    }
}


