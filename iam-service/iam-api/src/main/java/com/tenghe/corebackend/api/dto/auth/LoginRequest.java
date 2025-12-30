package com.tenghe.corebackend.api.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {
    private String identifier;
    private String password;
    private String captcha;
    private String captchaKey;
}
