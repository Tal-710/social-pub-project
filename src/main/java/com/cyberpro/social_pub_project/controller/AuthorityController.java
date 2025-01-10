package com.cyberpro.social_pub_project.controller;

import com.cyberpro.social_pub_project.entity.Authority;
import com.cyberpro.social_pub_project.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authorities")
public class AuthorityController {

    private final AuthorityService authorityService;

    @Autowired
    public AuthorityController(AuthorityService authorityService) {

        this.authorityService = authorityService;
    }

    @PostMapping("/update")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Authority> updateUserAuthority(
            @RequestParam String username,
            @RequestParam String newAuthority) {
        Authority updatedAuthority = authorityService.updateUserAuthority(username, newAuthority);
        return ResponseEntity.ok(updatedAuthority);
    }

    @GetMapping("/{username}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Authority> getUserAuthority(@PathVariable String username) {
        System.out.println();
        Authority authority = authorityService.getUserAuthority(username);
        System.out.println(authority);
        return ResponseEntity.ok(authority);
    }
}
