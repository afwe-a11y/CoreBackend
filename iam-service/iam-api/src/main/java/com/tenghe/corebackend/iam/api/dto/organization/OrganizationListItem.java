package com.tenghe.corebackend.iam.api.dto.organization;

public class OrganizationListItem {
    private String id;
    private String name;
    private long internalMemberCount;
    private long externalMemberCount;
    private String primaryAdminDisplay;
    private String status;
    private String createdDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
