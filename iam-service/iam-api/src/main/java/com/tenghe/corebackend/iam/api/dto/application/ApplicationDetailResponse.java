package com.tenghe.corebackend.iam.api.dto.application;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ApplicationDetailResponse {
  private String id;
  private String appName;
  private String appCode;
  private String description;
  private String status;
  private String createdDate;
  private List<Long> permissionIds;
}
