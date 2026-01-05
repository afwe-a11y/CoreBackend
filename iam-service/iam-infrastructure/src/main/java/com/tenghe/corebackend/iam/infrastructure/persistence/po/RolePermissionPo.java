package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class RolePermissionPo {
  private Long id;
  private Long appId;
  private Long roleId;
  private String permCode;
  private Instant createTime;
  private Integer deleted;
}
