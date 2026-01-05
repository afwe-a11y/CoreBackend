package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ApplicationPermissionPO {
  private Long id;
  private Long appId;
  private String permCode;
  private String status;
  private Instant createTime;
  private Integer deleted;
}
