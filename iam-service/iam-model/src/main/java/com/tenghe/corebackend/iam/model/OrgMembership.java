package com.tenghe.corebackend.iam.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class OrgMembership {
  private Long organizationId;
  private Long userId;
  private Instant createdAt;
  private boolean deleted;
}
