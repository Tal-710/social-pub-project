package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByIdNumber(Integer idNumber);

}
