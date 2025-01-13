package com.cyberpro.social_pub_project.controller;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class BarTenderController {

    @GetMapping("/bartender")
    public String showConfirmationPage() {
        try {
            return "bartender";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Bartender page not found");
        }
    }
}
