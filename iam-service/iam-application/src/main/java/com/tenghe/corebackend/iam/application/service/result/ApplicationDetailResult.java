package com.tenghe.corebackend.iam.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class ApplicationDetailResult {
  private Long id;
  private String appName;
  private String appCode;
  private String description;
  private String status;
  private Instant createdAt;
  private List<Long> permissionIds;
}
