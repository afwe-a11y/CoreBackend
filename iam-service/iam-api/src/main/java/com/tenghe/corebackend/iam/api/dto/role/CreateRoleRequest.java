package com.tenghe.corebackend.iam.api.dto.role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateRoleRequest {
  private Long appId;
  private String roleName;
  private String roleCode;
  private String description;
}
