package com.tenghe.corebackend.iam.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ApplicationPermission {
  private Long id;
  private Long appId;
  private Long permissionId;
  private Instant createdAt;
  private boolean deleted;
}
