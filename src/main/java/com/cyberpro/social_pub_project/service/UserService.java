package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(int id);

    User save(User user);

    void deleteById(int id);

    void registerUser(String username, String password, String first_name, String last_name, int age,int Id_number);

    Optional<User> findByUsername(String username);

    void addRoleUser(User user);

    Optional<User> findByIdNumber(Integer idNumber);

    void addUserRole(User user, String roleName);
    void removeUserRole(User user, String roleName);
}

