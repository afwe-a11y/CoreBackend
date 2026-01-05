package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateRoleCommand {
  private Long appId;
  private String roleName;
  private String roleCode;
  private String description;
}
