package com.backend.Service;

import com.backend.Entity.Organization;
import com.backend.Entity.Role;
import com.backend.Entity.User;
import com.backend.Entity.UserOrganizationRole;
import com.backend.Repository.OrganizationRepository;
import com.backend.Repository.RoleRepository;
import com.backend.Repository.UserOrganizationRoleRepository;
import com.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserOrganizationRoleRepository userOrganizationRoleRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    public User createUser(String email, String name) {
        // Create a new user with default role and organization
        User user = new User();
        user.setUserEmail(email);
        user.setUserName(name);
        user.setUserId(UUID.randomUUID().toString());

        // Set default role (admin for new users)
        Role role = roleRepository.findByRoleName("admin");
        //role.setRoleName("admin");
        //user.set(defaultRole);

        // Create a new organization for the user
        Organization organization = new Organization();
        organization.setOrganizationName("org-" + name);
        organization.setOrganizationId(UUID.randomUUID().toString());
        // Set other organization properties as needed
        //user.set(organization);

        // Save the user and organization to the database
        userRepository.save(user);
        organizationRepository.save(organization);

        // Update UserOrganizationRole table
        UserOrganizationRole userOrganizationRole = new UserOrganizationRole();
        userOrganizationRole.setUser(user);
        userOrganizationRole.setOrganization(organization);
        userOrganizationRole.setRole(role);
        userOrganizationRoleRepository.save(userOrganizationRole);

        return user;
    }

    public User loginUser(String email) {
        // Retrieve the user by email
        User user = getUserByEmail(email);

        // Return the user if found
        if (user != null) {
            return user;
        } else {
            return null;
        }
    }
}
