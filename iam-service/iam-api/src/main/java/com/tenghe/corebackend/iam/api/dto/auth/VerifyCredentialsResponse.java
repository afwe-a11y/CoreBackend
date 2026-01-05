package com.tenghe.corebackend.iam.api.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 凭证验证响应 DTO。
 * 返回用户基本信息，由 BFF 层生成 Token。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCredentialsResponse {

  /**
   * 用户 ID
   */
  private Long userId;

  /**
   * 用户名
   */
  private String username;

  /**
   * 姓名
   */
  private String name;

  /**
   * 邮箱
   */
  private String email;

  /**
   * 手机号
   */
  private String phone;

  /**
   * 是否需要重置密码（初始密码）
   */
  private boolean requirePasswordReset;

  /**
   * 用户状态
   */
  private String status;

  /**
   * 用户关联的组织 ID 列表
   */
  private List<Long> organizationIds;
}
