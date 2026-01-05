package com.tenghe.corebackend.iam.api.v1;

import com.tenghe.corebackend.iam.api.dto.auth.LoginRequest;
import com.tenghe.corebackend.iam.api.dto.auth.LoginResponse;
import com.tenghe.corebackend.iam.api.dto.auth.CaptchaResponse;
import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "认证")
public interface AuthApi {

  @Operation(summary = "登录获取Token")
  @PostMapping("/login")
  ApiResponse<LoginResponse> login(@RequestBody LoginRequest request);

  @Operation(summary = "获取验证码")
  @GetMapping("/captcha")
  ApiResponse<CaptchaResponse> getCaptcha();
}
