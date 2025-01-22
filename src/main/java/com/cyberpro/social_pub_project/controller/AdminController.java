package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;
    private final ProductService productService;
    private final QRCodeService qrCodeService;
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    public AdminController(UserService userService, RoleService roleService , ProductService productService, QRCodeService qrCodeService) {
        this.userService = userService;
        this.roleService = roleService;
        this.productService = productService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/")
    public String showAdminPanel(Model model) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("products", productService.findAll());
        return "admin";
    }

    @PostMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer id, @RequestParam int enabled) {
        logger.debug("Updating status for user ID: {} to enabled: {}", id, enabled);

        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            logger.debug("Current user state - QR Code: {}, Enabled: {}",
                    user.getQrCode() != null ? "exists" : "null",
                    user.getEnabled());


            if (enabled == 1 && user.getQrCode() == null) {
                logger.info("Generating new QR code for user ID: {}", id);
                try {
                    String qrCodeFileName = qrCodeService.generateAndUploadQRCode(user);
                    user.setQrCode(qrCodeFileName);
                    logger.info("Successfully generated QR code: {}", qrCodeFileName);
                } catch (Exception e) {
                    logger.error("Failed to generate QR code for user ID: {}", id, e);
                    return ResponseEntity.status(500).body("Failed to generate QR code");
                }
            } else {
                logger.debug("Skipping QR code generation. Enabled={}, Existing QR code={}",
                        enabled, user.getQrCode() != null ? "yes" : "no");
            }

            user.setEnabled(enabled);
            User savedUser = userService.save(user);
            logger.info("Successfully updated user status. ID: {}, Enabled: {}, QR Code: {}",
                    savedUser.getId(),
                    savedUser.getEnabled(),
                    savedUser.getQrCode() != null ? "exists" : "null");

            return ResponseEntity.ok().build();
        }
        logger.warn("User not found with ID: {}", id);
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/users/{id}/roles/add")
    public ResponseEntity<?> addUserRole(@PathVariable Integer id, @RequestParam String role) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            roleService.addUserRole(user.getUsername(), role);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{userId}/roles/{role}")
    public ResponseEntity<?> removeUserRole(@PathVariable Integer userId, @PathVariable String role) {
        try {
            Optional<User> userOptional = userService.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                roleService.removeUserRole(user.getUsername(), role);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
