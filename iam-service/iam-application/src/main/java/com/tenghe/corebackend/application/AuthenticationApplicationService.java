package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.LoginCommand;
import com.tenghe.corebackend.application.service.result.LoginResult;
import com.tenghe.corebackend.application.service.result.UserInfoResult;

public interface AuthenticationApplicationService {

    LoginResult login(LoginCommand command);

    UserInfoResult getUserInfo(Long userId);
}
