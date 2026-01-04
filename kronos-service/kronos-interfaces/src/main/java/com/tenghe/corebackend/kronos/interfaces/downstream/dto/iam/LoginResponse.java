package com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应 DTO。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  /**
   * 用户 ID
   */
  private String userId;

  /**
   * 用户名
   */
  private String username;

  /**
   * Token
   */
  private String token;

  /**
   * 是否需要重置密码
   */
  private boolean requirePasswordReset;
}
