package com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 凭证验证响应 DTO。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCredentialsResponse {

  private Long userId;
  private String username;
  private String name;
  private String email;
  private String phone;
  private boolean requirePasswordReset;
  private String status;
  private List<Long> organizationIds;
}
