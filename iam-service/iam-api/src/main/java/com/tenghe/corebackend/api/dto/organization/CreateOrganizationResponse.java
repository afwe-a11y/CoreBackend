package com.tenghe.corebackend.api.dto.organization;

public class CreateOrganizationResponse {
    private String id;

    public CreateOrganizationResponse() {
    }

    public CreateOrganizationResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
