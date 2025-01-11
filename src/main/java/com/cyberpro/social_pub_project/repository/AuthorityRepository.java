package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Authority.AuthorityId> {
    // You can add custom query methods here if needed
    Optional<Authority> findByUsername(String username);
}