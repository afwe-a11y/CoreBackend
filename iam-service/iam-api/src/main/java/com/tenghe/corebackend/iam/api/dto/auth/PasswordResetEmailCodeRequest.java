package com.tenghe.corebackend.iam.api.dto.auth;

public class PasswordResetEmailCodeRequest {
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
