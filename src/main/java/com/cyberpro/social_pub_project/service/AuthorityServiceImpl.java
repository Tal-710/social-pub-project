package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Authority;
import com.cyberpro.social_pub_project.repository.AuthorityRepository;
import com.cyberpro.social_pub_project.service.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.List;

// AuthorityServiceImpl.java


import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;
    private final UserRepository userRepository;

    @Autowired
    public AuthorityServiceImpl(AuthorityRepository authorityRepository, UserRepository userRepository) {
        this.authorityRepository = authorityRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Authority updateUserAuthority(String username, String newAuthority) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Authority authority = authorityRepository.findById(new Authority.AuthorityId(username, "ROLE_USER"))
                .orElse(new Authority(username, newAuthority, user));

        authority.getId().setAuthority(newAuthority);
        return authorityRepository.save(authority);
    }

    @Override
    public Authority getUserAuthority(String username) {
        return authorityRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authority not found for user: " + username));
    }
}