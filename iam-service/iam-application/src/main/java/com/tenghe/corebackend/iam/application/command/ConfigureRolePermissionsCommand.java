package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ConfigureRolePermissionsCommand {
  private Long roleId;
  private List<Long> permissionIds;
}
