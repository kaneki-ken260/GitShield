package com.backend.Repository;

import com.backend.Entity.Organization;
import com.backend.Entity.Role;
import com.backend.Entity.User;
import com.backend.Entity.UserOrganizationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserOrganizationRoleRepository extends JpaRepository<UserOrganizationRole, String> {

    UserOrganizationRole findByUser(User user);
    UserOrganizationRole findByOrganization(Organization organization);
    UserOrganizationRole findByRole(Role role);

}
