package com.tenghe.corebackend.kronos.infrastructure.adapter;

import com.tenghe.corebackend.kronos.infrastructure.feign.IamFeignClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.IamServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.IamApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.VerifyCredentialsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * IAM 服务适配器。
 * 封装对 iam-service 的调用。
 * 
 * Token 管理由 kronos-service 自行处理，iam-service 只提供凭证验证。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IamServiceAdapter implements IamServiceClient {

  private final IamFeignClient feignClient;

  @Override
  public IamApiResponse<VerifyCredentialsResponse> verifyCredentials(VerifyCredentialsRequest request) {
    log.info("CALL iam-service verifyCredentials identifier={}", request.getIdentifier());
    long start = System.currentTimeMillis();
    var response = feignClient.verifyCredentials(request);
    log.info("CALL iam-service verifyCredentials costMs={} success={}", 
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public IamApiResponse<Boolean> checkUserStatus(Long userId) {
    log.debug("CALL iam-service checkUserStatus userId={}", userId);
    long start = System.currentTimeMillis();
    var response = feignClient.checkUserStatus(userId);
    log.debug("CALL iam-service checkUserStatus costMs={} success={}", 
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }
}
