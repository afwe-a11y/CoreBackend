package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ApplicationTemplatePO {
  private Long id;
  private String templateCode;
  private String templateName;
  private String description;
  private String status;
  private Instant createTime;
  private Integer deleted;
}
