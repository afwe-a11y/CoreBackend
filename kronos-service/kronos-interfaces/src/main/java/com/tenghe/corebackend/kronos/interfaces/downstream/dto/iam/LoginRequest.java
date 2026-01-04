package com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  /**
   * 用户标识（用户名/手机号/邮箱）
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
