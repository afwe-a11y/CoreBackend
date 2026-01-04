package com.tenghe.corebackend.iam.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 凭证验证请求 DTO。
 * 用于 BFF 层调用基底服务验证用户凭证。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCredentialsRequest {
  
  /**
   * 登录标识（用户名/邮箱/手机号）
   */
  private String identifier;
  
  /**
   * 密码
   */
  private String password;
  
  /**
   * 验证码
   */
  private String captcha;
  
  /**
   * 验证码 Key
   */
  private String captchaKey;
}
