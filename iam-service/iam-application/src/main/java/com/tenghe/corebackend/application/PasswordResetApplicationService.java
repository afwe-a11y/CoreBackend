package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.ResetPasswordCommand;
import com.tenghe.corebackend.application.command.SendEmailCodeCommand;

public interface PasswordResetApplicationService {

    void sendEmailCode(SendEmailCodeCommand command);

    void resetPassword(ResetPasswordCommand command);
}
