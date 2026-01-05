package com.tenghe.corebackend.iam.api.dto.role;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ConfigureRolePermissionsRequest {
  private List<Long> permissionIds;
}
