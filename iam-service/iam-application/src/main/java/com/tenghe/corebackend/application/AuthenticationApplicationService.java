package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.LoginCommand;
import com.tenghe.corebackend.application.service.result.LoginResult;

public interface AuthenticationApplicationService {

    LoginResult login(LoginCommand command);

    void logout(String token);

    Long validateSession(String token);

    String generateCaptcha(String key);
}
