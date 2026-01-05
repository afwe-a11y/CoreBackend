package com.tenghe.corebackend.iam.model;

import com.tenghe.corebackend.iam.model.enums.RoleCategoryEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class RoleGrant {
  private Long id;
  private Long organizationId;
  private Long userId;
  private Long appId;
  private Long roleId;
  private String roleCode;
  private RoleCategoryEnum roleCategory;
  private Instant createdAt;
  private boolean deleted;
}
