// RoleService.java
package com.cyberpro.social_pub_project.service;

import com.cyberpro.social_pub_project.entity.Role;
import java.util.List;
import java.util.Set;

public interface RoleService {
    Role updateUserRole(String username, String newRole);
    Set<Role> getUserRoles(String username);
    void addUserRole(String username, String role);
    void removeUserRole(String username, String role);
}

