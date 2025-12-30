package com.tenghe.corebackend.controller.web;

import com.tenghe.corebackend.api.dto.auth.ResetPasswordRequest;
import com.tenghe.corebackend.api.dto.auth.SendEmailCodeRequest;
import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.application.command.ResetPasswordCommand;
import com.tenghe.corebackend.application.command.SendEmailCodeCommand;
import com.tenghe.corebackend.application.PasswordResetApplicationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 密码重置相关 HTTP 入口，面向服务间调用。
 */
@RestController
@RequestMapping("/api/password")
public class PasswordController {
    private final PasswordResetApplicationService passwordResetService;

    public PasswordController(PasswordResetApplicationService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    /**
     * 发送邮箱验证码。
     */
    @PostMapping("/send-code")
    public ApiResponse<Void> sendEmailCode(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody SendEmailCodeRequest request) {
        SendEmailCodeCommand command = new SendEmailCodeCommand();
        command.setUserId(userId);
        command.setEmail(request.getEmail());
        passwordResetService.sendEmailCode(command);
        return ApiResponse.ok(null);
    }

    /**
     * 重置密码。
     */
    @PostMapping("/reset")
    public ApiResponse<Void> resetPassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody ResetPasswordRequest request) {
        ResetPasswordCommand command = new ResetPasswordCommand();
        command.setUserId(userId);
        command.setOldPassword(request.getOldPassword());
        command.setNewPassword(request.getNewPassword());
        command.setEmailCode(request.getEmailCode());
        passwordResetService.resetPassword(command);
        return ApiResponse.ok(null);
    }
}
