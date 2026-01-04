package com.tenghe.corebackend.kronos.infrastructure.feign;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.IamApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * IAM 服务 Feign 客户端。
 * 调用 iam-service 的认证接口。
 * 
 * Token 管理由 kronos-service 自行处理，iam-service 只提供凭证验证。
 */
@FeignClient(name = "iam-service", contextId = "iamClient", path = "/api/auth")
public interface IamFeignClient {

  /**
   * 验证用户凭证（不生成 Token）
   */
  @PostMapping("/verify-credentials")
  IamApiResponse<VerifyCredentialsResponse> verifyCredentials(@RequestBody VerifyCredentialsRequest request);

  /**
   * 检查用户状态
   */
  @GetMapping("/check-user-status/{userId}")
  IamApiResponse<Boolean> checkUserStatus(@PathVariable("userId") Long userId);
}
