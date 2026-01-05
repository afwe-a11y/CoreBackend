package com.tenghe.corebackend.iam.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class RoleListItemResult {
  private Long id;
  private Long appId;
  private String appName;
  private String roleName;
  private String roleCode;
  private String description;
  private String status;
  private boolean preset;
  private int memberCount;
  private Instant createdAt;
}
