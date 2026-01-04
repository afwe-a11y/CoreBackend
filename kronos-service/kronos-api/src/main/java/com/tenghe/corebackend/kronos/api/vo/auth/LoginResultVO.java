package com.tenghe.corebackend.kronos.api.vo.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录结果 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResultVO {

  /**
   * 用户 ID
   */
  private String userId;

  /**
   * 用户名
   */
  private String username;

  /**
   * 姓名
   */
  private String name;

  /**
   * Token
   */
  private String token;

  /**
   * 是否需要重置密码
   */
  private boolean requirePasswordReset;
}
