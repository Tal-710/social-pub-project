package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEncryptedIdNumber(String encryptedIdNumber);
}

