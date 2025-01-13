package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.service.AuthorityService;
import com.cyberpro.social_pub_project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AuthorityService authorityService;

    public AdminController(UserService userService, AuthorityService authorityService) {
        this.userService = userService;
        this.authorityService = authorityService;
    }

    @GetMapping()
    public String showAdminPanel(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        return "admin";
    }

    @PostMapping("/users/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateUserStatus(@PathVariable Integer id, @RequestParam int enabled) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(enabled);
            userService.save(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/users/{id}/roles/add")
    @ResponseBody
    public ResponseEntity<?> addUserRole(@PathVariable Integer id, @RequestParam String role) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            authorityService.addUserAuthority(user.getUsername(), role);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{userId}/roles/{role}")
    @ResponseBody
    public ResponseEntity<?> removeUserRole(@PathVariable Integer userId, @PathVariable String role) {
        try {
            Optional<User> userOptional = userService.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                authorityService.removeUserAuthority(user.getUsername(), role);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/users/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            Optional<User> userOptional = userService.findById(id);
            if (userOptional.isPresent()) {
                userService.deleteById(id);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
