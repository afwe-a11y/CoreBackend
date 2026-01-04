package com.tenghe.corebackend.iam.application.service.result;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 凭证验证结果。
 * 返回用户基本信息，不包含 Token（Token 由 BFF 层生成）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCredentialsResult {
  
  private Long userId;
  private String username;
  private String name;
  private String email;
  private String phone;
  private boolean requirePasswordReset;
  private String status;
  private List<Long> organizationIds;
}
