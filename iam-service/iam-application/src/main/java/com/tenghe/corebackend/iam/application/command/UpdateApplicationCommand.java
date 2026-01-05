package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UpdateApplicationCommand {
  private Long appId;
  private String appName;
  private String description;
  private String status;
  private List<Long> permissionIds;
}
