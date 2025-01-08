package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(int theId);

    User save(User theEmployee);

    void deleteById(int theId);
}
