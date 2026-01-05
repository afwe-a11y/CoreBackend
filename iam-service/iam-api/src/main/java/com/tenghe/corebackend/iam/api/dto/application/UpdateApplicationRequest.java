package com.tenghe.corebackend.iam.api.dto.application;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateApplicationRequest {
  private String appName;
  private String description;
  private String status;
  private List<Long> permissionIds;
}
