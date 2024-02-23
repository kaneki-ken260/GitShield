package com.backend.Repository;

import com.backend.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByRoleName(String roleName);

    Role findByRoleId(String roleId);

}
