package com.tenghe.corebackend.iam.api.dto.role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleListItem {
  private String id;
  private String appId;
  private String appName;
  private String roleName;
  private String roleCode;
  private String description;
  private String status;
  private boolean preset;
  private int memberCount;
  private String createdDate;
}
