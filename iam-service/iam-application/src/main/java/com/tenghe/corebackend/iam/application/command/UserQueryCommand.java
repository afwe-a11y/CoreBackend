package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserQueryCommand {
  private String keyword;
  private String status;
  private List<Long> organizationIds;
  private List<String> roleCodes;
  private Integer page;
  private Integer size;
}
