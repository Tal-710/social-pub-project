package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll(); // Fixed naming convention

    Optional<User> findById(int id); // Renamed parameter for clarity

    User save(User user); // Renamed parameter for clarity

    void deleteById(int id);// Renamed parameter for clarity

    void registerUser(String username, String password, String first_name, String last_name, int age,int Id_number);

    Optional<User> findByUsername(String username);
}

