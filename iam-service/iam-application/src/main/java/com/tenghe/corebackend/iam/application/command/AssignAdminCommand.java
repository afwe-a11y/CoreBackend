package com.tenghe.corebackend.iam.application.command;

public class AssignAdminCommand {
    private Long organizationId;
    private Long userId;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
