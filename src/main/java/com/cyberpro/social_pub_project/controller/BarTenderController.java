package com.cyberpro.social_pub_project.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BarTenderController {

    @GetMapping("/bartender")
        public String showBarTender(){

            return "bartender";

    }
}
