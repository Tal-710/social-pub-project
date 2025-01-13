package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.Authority;
import com.cyberpro.social_pub_project.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/authorities")
public class AuthorityController {

    private final AuthorityService authorityService;

    @Autowired
    public AuthorityController(AuthorityService authorityService) {
        this.authorityService = authorityService;
    }

    @PostMapping("/update")
    public ResponseEntity<Authority> updateUserAuthority(
            @RequestParam String username,
            @RequestParam String newAuthority) {
        try {
            Authority updatedAuthority = authorityService.updateUserAuthority(username, newAuthority);
            if (updatedAuthority != null) {
                return ResponseEntity.ok(updatedAuthority);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with username: " + username);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error updating authority: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<List<Authority>> getUserAuthority(@PathVariable String username) {
        try {
            List<Authority> authority = authorityService.getUserAuthority(username);
            if (authority != null) {
                return ResponseEntity.ok(authority);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Authority not found for username: " + username);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving authority: " + e.getMessage());
        }
    }
}
