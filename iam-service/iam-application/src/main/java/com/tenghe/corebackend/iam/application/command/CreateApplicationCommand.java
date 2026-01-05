package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateApplicationCommand {
  private String appName;
  private String appCode;
  private String description;
  private List<Long> permissionIds;
}
