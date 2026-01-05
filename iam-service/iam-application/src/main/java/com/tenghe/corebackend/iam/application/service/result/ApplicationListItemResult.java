package com.tenghe.corebackend.iam.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ApplicationListItemResult {
  private Long id;
  private String appName;
  private String appCode;
  private String description;
  private String status;
  private Instant createdAt;
}
