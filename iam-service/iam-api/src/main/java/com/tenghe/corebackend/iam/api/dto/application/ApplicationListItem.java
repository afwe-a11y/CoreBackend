package com.tenghe.corebackend.iam.api.dto.application;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationListItem {
  private String id;
  private String appName;
  private String appCode;
  private String description;
  private String status;
  private String createdDate;
}
