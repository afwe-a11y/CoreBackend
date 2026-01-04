package com.tenghe.corebackend.kronos.interfaces.downstream;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.IamApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsResponse;

/**
 * IAM 服务客户端接口。
 * 定义调用 iam-service 的端口。
 * 
 * 注意：Token 管理由 kronos-service（BFF）自行处理，iam-service 只提供凭证验证和用户状态检查。
 */
public interface IamServiceClient {

  /**
   * 验证用户凭证
   * 仅验证凭证，返回用户信息，不涉及 Token。
   *
   * @param request 凭证验证请求
   * @return 用户信息
   */
  IamApiResponse<VerifyCredentialsResponse> verifyCredentials(VerifyCredentialsRequest request);

  /**
   * 检查用户状态
   * 验证 Token 后调用，检查用户是否仍然有效（未删除、未禁用）。
   *
   * @param userId 用户 ID
   * @return 是否有效
   */
  IamApiResponse<Boolean> checkUserStatus(Long userId);
}
