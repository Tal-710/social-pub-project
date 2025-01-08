package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
