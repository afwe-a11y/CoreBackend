package com.tenghe.corebackend.api.dto.member;

import java.util.List;

public class CreateInternalMemberRequest {
    private String username;
    private String name;
    private String phone;
    private String email;
    private List<Long> organizationIds;
    private List<RoleSelectionDto> roleSelections;
    private String status;
    private String accountType;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Long> getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(List<Long> organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<RoleSelectionDto> getRoleSelections() {
        return roleSelections;
    }

    public void setRoleSelections(List<RoleSelectionDto> roleSelections) {
        this.roleSelections = roleSelections;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
