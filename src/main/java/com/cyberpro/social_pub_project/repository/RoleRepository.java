package com.cyberpro.social_pub_project.repository;

import com.cyberpro.social_pub_project.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
   Optional<Role> findByRoleName(String roleName);
}
//   @Modifying
//   @Query("DELETE FROM Authority a WHERE a.id.username = :username AND a.id.authority = :role")
//   void deleteByUsernameAndRole(@Param("username") String username, @Param("role") String role);
//}