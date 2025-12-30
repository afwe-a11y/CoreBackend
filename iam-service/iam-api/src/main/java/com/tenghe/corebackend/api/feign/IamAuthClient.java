package com.tenghe.corebackend.api.feign;

import com.tenghe.corebackend.api.dto.auth.CaptchaResponse;
import com.tenghe.corebackend.api.dto.auth.LoginRequest;
import com.tenghe.corebackend.api.dto.auth.LoginResponse;
import com.tenghe.corebackend.api.dto.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * IAM 认证服务 Feign Client
 * 供上层微服务调用认证相关功能
 */
@FeignClient(name = "iam-service", path = "/api/auth", contextId = "iamAuthClient")
public interface IamAuthClient {

    /**
     * 获取验证码
     */
    @GetMapping("/captcha")
    ApiResponse<CaptchaResponse> getCaptcha();

    /**
     * 用户登录
     */
    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginRequest request);

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestHeader("Authorization") String token);

    /**
     * 验证会话有效性
     */
    @GetMapping("/validate")
    ApiResponse<Boolean> validateSession(@RequestHeader("Authorization") String token);
}
