package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.Role;
import com.cyberpro.social_pub_project.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@RestController
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/update")
    public ResponseEntity<Role> updateUserRole(
            @RequestParam String username,
            @RequestParam String newRole) {
        try {
            Role updatedRole = roleService.updateUserRole(username, newRole);
            if (updatedRole != null) {
                return ResponseEntity.ok(updatedRole);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with username: " + username);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Error updating role: " + e.getMessage());
        }
    }

    @GetMapping("/{username}")
    public ResponseEntity<Set<Role>> getUserRoles(@PathVariable String username) {
        try {
            Set<Role> roles = roleService.getUserRoles(username);
            if (!roles.isEmpty()) {
                return ResponseEntity.ok(roles);
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Roles not found for username: " + username);
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error retrieving roles: " + e.getMessage());
        }
    }
}
