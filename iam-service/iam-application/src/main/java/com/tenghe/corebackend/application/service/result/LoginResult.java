package com.tenghe.corebackend.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResult {
    private Long userId;
    private String username;
    private String token;
    private boolean requirePasswordReset;
}
