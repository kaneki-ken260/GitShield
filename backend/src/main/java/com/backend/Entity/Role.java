package com.backend.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="role")
public class Role {
    @Id
    @GeneratedValue
    @Column(name="role_id")
    private String roleId;

    private String roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOrganizationRole> userOrganizationRoleList = new ArrayList<>();

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public List<UserOrganizationRole> getOrganizationRoleList() {
        return userOrganizationRoleList;
    }

    public void setOrganizationRoleList(List<UserOrganizationRole> userOrganizationRoleList) {
        this.userOrganizationRoleList = userOrganizationRoleList;
    }
}

