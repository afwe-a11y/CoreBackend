package com.tenghe.corebackend.iam.controller.web;

import com.tenghe.corebackend.iam.api.dto.auth.CaptchaResponse;
import com.tenghe.corebackend.iam.api.dto.auth.LoginRequest;
import com.tenghe.corebackend.iam.api.dto.auth.LoginResponse;
import com.tenghe.corebackend.iam.api.dto.auth.VerifyCredentialsRequest;
import com.tenghe.corebackend.iam.api.dto.auth.VerifyCredentialsResponse;
import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.application.command.LoginCommand;
import com.tenghe.corebackend.iam.application.command.VerifyCredentialsCommand;
import com.tenghe.corebackend.iam.application.AuthenticationApplicationService;
import com.tenghe.corebackend.iam.application.service.result.LoginResult;
import com.tenghe.corebackend.iam.application.service.result.VerifyCredentialsResult;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关的 HTTP 入口，面向服务间调用。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationApplicationService authService;

    public AuthController(AuthenticationApplicationService authService) {
        this.authService = authService;
    }

    /**
     * 获取登录验证码。
     */
    @GetMapping("/captcha")
    public ApiResponse<CaptchaResponse> getCaptcha() {
        String captchaKey = UUID.randomUUID().toString();
        String captchaCode = authService.generateCaptcha(captchaKey);
        return ApiResponse.ok(new CaptchaResponse(captchaKey, captchaCode));
    }

    /**
     * 登录并生成访问令牌。
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand();
        command.setIdentifier(request.getIdentifier());
        command.setPassword(request.getPassword());
        command.setCaptcha(request.getCaptcha());
        command.setCaptchaKey(request.getCaptchaKey());
        LoginResult result = authService.login(command);

        LoginResponse response = new LoginResponse();
        response.setUserId(String.valueOf(result.getUserId()));
        response.setUsername(result.getUsername());
        response.setToken(result.getToken());
        response.setRequirePasswordReset(result.isRequirePasswordReset());
        return ApiResponse.ok(response);
    }

    /**
     * 注销当前令牌。
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        authService.logout(token);
        return ApiResponse.ok(null);
    }

    /**
     * 校验当前令牌有效性。
     */
    @GetMapping("/validate")
    public ApiResponse<Boolean> validateSession(@RequestHeader(value = "Authorization", required = false) String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Long userId = authService.validateSession(token);
        return ApiResponse.ok(userId != null);
    }

    /**
     * 验证用户凭证（供 BFF 层调用）。
     * 仅验证凭证，不生成 Token。Token 由 BFF 层管理。
     */
    @PostMapping("/verify-credentials")
    public ApiResponse<VerifyCredentialsResponse> verifyCredentials(@RequestBody VerifyCredentialsRequest request) {
        VerifyCredentialsCommand command = new VerifyCredentialsCommand();
        command.setIdentifier(request.getIdentifier());
        command.setPassword(request.getPassword());
        command.setCaptcha(request.getCaptcha());
        command.setCaptchaKey(request.getCaptchaKey());
        
        VerifyCredentialsResult result = authService.verifyCredentials(command);
        
        VerifyCredentialsResponse response = VerifyCredentialsResponse.builder()
            .userId(result.getUserId())
            .username(result.getUsername())
            .name(result.getName())
            .email(result.getEmail())
            .phone(result.getPhone())
            .requirePasswordReset(result.isRequirePasswordReset())
            .status(result.getStatus())
            .organizationIds(result.getOrganizationIds())
            .build();
        return ApiResponse.ok(response);
    }

    /**
     * 检查用户状态（供 BFF 层验证 Token 后调用）。
     */
    @GetMapping("/check-user-status/{userId}")
    public ApiResponse<Boolean> checkUserStatus(@PathVariable Long userId) {
        boolean valid = authService.checkUserStatus(userId);
        return ApiResponse.ok(valid);
    }
}
