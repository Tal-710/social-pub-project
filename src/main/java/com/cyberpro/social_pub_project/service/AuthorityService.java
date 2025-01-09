package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Authority;

import java.util.List;

public interface AuthorityService {
    Authority updateUserAuthority(String username, String newAuthority);
    Authority getUserAuthority(String username);
}

