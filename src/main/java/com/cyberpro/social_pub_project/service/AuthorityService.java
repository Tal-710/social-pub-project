package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Authority;

import java.util.List;

public interface AuthorityService {

    Authority updateUserAuthority(String username, String newAuthority);

    List<Authority> getUserAuthority(String username);

    void addUserAuthority(String username, String role);

    void removeUserAuthority(String username, String role);
}

