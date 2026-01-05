package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CreateUserCommand {
  private String username;
  private String name;
  private String phone;
  private String email;
  private List<Long> organizationIds;
  private List<RoleSelectionCommand> roleSelections;
}
