package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BatchRoleMemberCommand {
  private Long roleId;
  private Long organizationId;
  private List<Long> userIds;
}
