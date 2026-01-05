package com.tenghe.corebackend.iam.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ExternalMembership {
  private Long organizationId;
  private Long userId;
  private Long sourceOrganizationId;
  private Instant createdAt;
  private boolean deleted;
}
