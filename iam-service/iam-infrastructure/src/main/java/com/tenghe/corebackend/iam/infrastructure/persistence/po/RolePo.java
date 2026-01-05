package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class RolePO {
  private Long id;
  private Long appId;
  private String roleCode;
  private String roleName;
  private String roleType;
  private String description;
  private Instant createTime;
  private Integer deleted;
}
