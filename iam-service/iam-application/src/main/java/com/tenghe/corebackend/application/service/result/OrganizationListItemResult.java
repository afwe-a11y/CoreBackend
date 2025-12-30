package com.tenghe.corebackend.application.service.result;

import java.time.Instant;

public class OrganizationListItemResult {
    private Long id;
    private String name;
    private long internalMemberCount;
    private long externalMemberCount;
    private String primaryAdminDisplay;
    private String status;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getInternalMemberCount() {
        return internalMemberCount;
    }

    public void setInternalMemberCount(long internalMemberCount) {
        this.internalMemberCount = internalMemberCount;
    }

    public long getExternalMemberCount() {
        return externalMemberCount;
    }

    public void setExternalMemberCount(long externalMemberCount) {
        this.externalMemberCount = externalMemberCount;
    }

    public String getPrimaryAdminDisplay() {
        return primaryAdminDisplay;
    }

    public void setPrimaryAdminDisplay(String primaryAdminDisplay) {
        this.primaryAdminDisplay = primaryAdminDisplay;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
