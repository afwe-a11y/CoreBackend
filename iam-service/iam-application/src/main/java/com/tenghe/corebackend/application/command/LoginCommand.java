package com.tenghe.corebackend.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginCommand {
    private String identifier;
    private String password;
    private String captcha;
    private String captchaKey;
}
