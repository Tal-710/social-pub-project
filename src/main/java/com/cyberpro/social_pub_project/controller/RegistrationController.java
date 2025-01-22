package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.service.AzureBlobService;
import com.cyberpro.social_pub_project.service.ReCaptchaService;
import com.cyberpro.social_pub_project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private final UserService userService;
    private final ReCaptchaService reCaptchaService;
    private final AzureBlobService azureBlobService;

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB in bytes
    private static final int MAX_WIDTH = 800;
    private static final int MAX_HEIGHT = 800;

    public RegistrationController(UserService userService, ReCaptchaService reCaptchaService ,AzureBlobService azureBlobService) {
        this.userService = userService;
        this.reCaptchaService = reCaptchaService;
        this.azureBlobService = azureBlobService;
    }
    private boolean validateImageDimensions(byte[] imageBytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
            BufferedImage image = ImageIO.read(bis);

            if (image == null) {
                return false;
            }

            return image.getWidth() <= MAX_WIDTH && image.getHeight() <= MAX_HEIGHT;
        } catch (IOException e) {
            return false;
        }
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               @RequestParam("g-recaptcha-response") String captchaResponse,
                               @RequestParam(value = "profilePicData", required = false) String profilePicData,
                               Model model) {
        try {

            System.out.println("Received user: " + user);
            System.out.println("ID Number: " + user.getIdNumber());

            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(error ->
                        System.err.println("Validation Error: " + error.getDefaultMessage())
                );
            }
            if (reCaptchaService.validateCaptcha(captchaResponse)) {
                model.addAttribute("errorMessage", "Please verify that you are not a robot");
                return "register";
            }

            if (userService.findByUsername(user.getUsername()).isPresent()) {
                bindingResult.rejectValue("username", "error.user", "Username already exists");
            }
            if (userService.findByIdNumber(user.getIdNumber()).isPresent()) {
                bindingResult.rejectValue("idNumber", "error.user", "ID Number already exists");
            }
            if (bindingResult.hasErrors()) {
                return "register";
            }

            String profilePictureFileName = null;
            if (profilePicData != null && !profilePicData.isEmpty()) {
                try {
                    String base64Image = profilePicData.substring(profilePicData.indexOf(",") + 1);
                    byte[] imageBytes = Base64.getDecoder().decode(base64Image);

                    // Validate image size
                    if (imageBytes.length > MAX_IMAGE_SIZE) {
                        model.addAttribute("errorMessage", "Profile picture size exceeds maximum allowed size (5MB)");
                        return "register";
                    }

                    String fileName = "profile_" + UUID.randomUUID().toString() + ".jpg";

                    azureBlobService.uploadProfilePicture(imageBytes, fileName);
                    profilePictureFileName = fileName;

                } catch (Exception e) {
                    model.addAttribute("errorMessage", "Failed to process profile picture: " + e.getMessage());
                    return "register";
                }
            }

            userService.registerUser(
                    user.getUsername(),
                    user.getPassword(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getAge(),
                    user.getIdNumber(),
                    profilePictureFileName
            );

            return "redirect:/login";
        } catch (Exception e) {
            System.err.println("Registration failed: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("errorMessage", "Registration failed. Please try again later.");
            return "register";
        }
    }
}