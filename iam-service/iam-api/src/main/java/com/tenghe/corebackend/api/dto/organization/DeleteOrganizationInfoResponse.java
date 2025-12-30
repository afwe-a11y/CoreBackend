package com.tenghe.corebackend.api.dto.organization;

public class DeleteOrganizationInfoResponse {
    private String name;
    private long userCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }
}
