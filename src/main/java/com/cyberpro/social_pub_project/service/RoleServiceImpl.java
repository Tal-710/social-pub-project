// RoleServiceImpl.java
package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Role;
import com.cyberpro.social_pub_project.entity.User;
import com.cyberpro.social_pub_project.repository.RoleRepository;
import com.cyberpro.social_pub_project.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Role updateUserRole(String username, String newRole) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Role role = roleRepository.findByRoleName(newRole)
                .orElseThrow(() -> new RuntimeException("Role not found: " + newRole));

        user.getRoles().add(role);
        userRepository.save(user);

        return role;
    }

    @Override
    public Set<Role> getUserRoles(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return user.getRoles();
    }

    @Override
    @Transactional
    public void addUserRole(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        if (user.getRoles().stream()
                .noneMatch(r -> r.getRoleName().equals(roleName))) {
            user.getRoles().add(role);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void removeUserRole(String username, String roleName) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getRoles().removeIf(role -> role.getRoleName().equals(roleName));
        userRepository.save(user);
    }
}