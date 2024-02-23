package com.backend.Entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="organization")
public class Organization {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "organization_id", updatable = false, nullable = false)
    private String organizationId;

    private String organizationName;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOrganizationRole> userOrganizationRoleList = new ArrayList<>();

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public List<UserOrganizationRole> getOrganizationRoleList() {
        return userOrganizationRoleList;
    }

    public void setOrganizationRoleList(List<UserOrganizationRole> userOrganizationRoleList) {
        this.userOrganizationRoleList = userOrganizationRoleList;
    }
}

