package com.tenghe.corebackend.iam.api.dto.member;

public class CreateInternalMemberResponse {
    private String username;
    private String phone;

    public CreateInternalMemberResponse() {
    }

    public CreateInternalMemberResponse(String username, String phone) {
        this.username = username;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
