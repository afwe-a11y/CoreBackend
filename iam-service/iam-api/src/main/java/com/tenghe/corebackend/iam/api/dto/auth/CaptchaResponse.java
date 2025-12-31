package com.tenghe.corebackend.iam.api.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CaptchaResponse {
    private String captchaKey;
    private String captchaImage;

    public CaptchaResponse(String captchaKey, String captchaImage) {
        this.captchaKey = captchaKey;
        this.captchaImage = captchaImage;
    }
}
