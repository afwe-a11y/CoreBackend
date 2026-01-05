package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class UserOrgPo {
  private Long id;
  private Long userId;
  private Long orgId;
  private String identityType;
  private String status;
  private Instant joinTime;
  private Instant createTime;
  private Integer deleted;
  private Long sourceOrgId;
}
