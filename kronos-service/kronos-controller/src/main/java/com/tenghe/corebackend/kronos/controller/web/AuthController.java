package com.tenghe.corebackend.kronos.controller.web;

import com.tenghe.corebackend.kronos.api.common.ApiResponse;
import com.tenghe.corebackend.kronos.api.vo.auth.LoginResultVO;
import com.tenghe.corebackend.kronos.interfaces.TokenServicePort;
import com.tenghe.corebackend.kronos.interfaces.downstream.IamServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.IamApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器（BFF 层）。
 * 调用 iam-service 验证凭证，在本地（BFF）生成和管理 Token。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final IamServiceClient iamServiceClient;
  private final TokenServicePort tokenService;

  /**
   * 用户登录
   * 1. 调用 iam-service 验证凭证
   * 2. 在本地（BFF）生成 Token 并存储到 Redis
   */
  @PostMapping("/login")
  public ApiResponse<LoginResultVO> login(@RequestBody VerifyCredentialsRequest request) {
    log.info("REQ POST /api/v1/auth/login identifier={}", request.getIdentifier());
    long start = System.currentTimeMillis();

    IamApiResponse<VerifyCredentialsResponse> iamResponse = iamServiceClient.verifyCredentials(request);
    
    if (!iamResponse.isOk()) {
      log.warn("登录失败: identifier={} message={}", request.getIdentifier(), iamResponse.getMessage());
      return ApiResponse.error(iamResponse.getCode(), iamResponse.getMessage());
    }

    VerifyCredentialsResponse userInfo = iamResponse.getData();
    
    String token = tokenService.generateToken(userInfo.getUserId(), userInfo.getUsername());

    LoginResultVO result = LoginResultVO.builder()
        .userId(String.valueOf(userInfo.getUserId()))
        .username(userInfo.getUsername())
        .name(userInfo.getName())
        .token(token)
        .requirePasswordReset(userInfo.isRequirePasswordReset())
        .build();

    log.info("RES POST /api/v1/auth/login userId={} costMs={}", 
        userInfo.getUserId(), System.currentTimeMillis() - start);
    return ApiResponse.success(result);
  }

  /**
   * 用户登出
   * 使本地 Token 失效
   */
  @PostMapping("/logout")
  public ApiResponse<Void> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
    log.info("REQ POST /api/v1/auth/logout");
    long start = System.currentTimeMillis();

    if (authorization == null || authorization.isEmpty()) {
      return ApiResponse.success();
    }

    String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;
    tokenService.invalidateToken(token);

    log.info("RES POST /api/v1/auth/logout costMs={}", System.currentTimeMillis() - start);
    return ApiResponse.success();
  }
}
