package com.tenghe.corebackend.iam.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class RolePermission {
  private Long id;
  private Long roleId;
  private Long permissionId;
  private Instant createdAt;
  private boolean deleted;
}
