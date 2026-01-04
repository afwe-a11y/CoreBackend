package com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 凭证验证请求 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCredentialsRequest {

  private String identifier;
  private String password;
  private String captcha;
  private String captchaKey;
}
