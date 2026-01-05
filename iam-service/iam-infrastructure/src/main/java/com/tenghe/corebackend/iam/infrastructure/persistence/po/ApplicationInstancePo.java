package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ApplicationInstancePo {
  private Long id;
  private Long orgId;
  private Long templateId;
  private String appCode;
  private String appName;
  private String status;
  private Instant createTime;
  private Integer deleted;
}
