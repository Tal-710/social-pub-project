package com.cyberpro.social_pub_project.controller;


import com.cyberpro.social_pub_project.entity.Product;
import com.cyberpro.social_pub_project.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class BartenderController {

    private final ProductService productService;

    @Autowired
    public BartenderController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/bartender")
    public String showOrderPage(Model model) {
        try {
            List<Product> products = productService.findAll();
            model.addAttribute("products", products);
            return "bartender";
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to load bartender page");
        }
    }
}
