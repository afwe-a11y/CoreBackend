package com.tenghe.corebackend.iam.api.dto.role;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RoleDetailResponse {
  private String id;
  private String appId;
  private String appName;
  private String roleName;
  private String roleCode;
  private String description;
  private String status;
  private boolean preset;
  private String createdDate;
  private List<Long> permissionIds;
}
