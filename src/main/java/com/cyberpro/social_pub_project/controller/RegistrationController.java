package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showRegistrationForm() {
        return "register"; // Points to register.html
    }

    @PostMapping
    public String registerUser(@ModelAttribute User user) {
        userService.registerUser(user.getUsername(), user.getPassword(), user.getFirstName(),
                user.getLastName(), user.getAge(), user.getIdNumber());
        return "login";
    }
}


