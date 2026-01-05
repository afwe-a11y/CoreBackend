package com.tenghe.corebackend.iam.api.dto.application;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateApplicationRequest {
  private String appName;
  private String appCode;
  private String description;
  private List<Long> permissionIds;
}
