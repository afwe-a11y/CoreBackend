package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class UserRolePo {
  private Long id;
  private Long userId;
  private Long orgId;
  private Long appId;
  private Long roleId;
  private String assetScope;
  private Instant createTime;
  private Integer deleted;
  private String roleCode;
}
