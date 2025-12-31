package com.tenghe.corebackend.iam.api.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordRequest {
    private String oldPassword;
    private String newPassword;
    private String emailCode;
}
