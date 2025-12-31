package com.tenghe.corebackend.iam.application.command;

import java.util.List;

public class CreateInternalMemberCommand {
    private Long organizationId;
    private String username;
    private String name;
    private String phone;
    private String email;
    private List<Long> organizationIds;
    private List<RoleSelectionCommand> roleSelections;
    private String status;
    private String accountType;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

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

    public List<RoleSelectionCommand> getRoleSelections() {
        return roleSelections;
    }

    public void setRoleSelections(List<RoleSelectionCommand> roleSelections) {
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
