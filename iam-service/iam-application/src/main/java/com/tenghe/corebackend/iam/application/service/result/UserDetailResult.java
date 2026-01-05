package com.tenghe.corebackend.iam.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDetailResult {
  private Long id;
  private String username;
  private String name;
  private String phone;
  private String email;
  private String status;
  private Instant createdAt;
  private List<Long> organizationIds;
  private List<RoleSelectionResult> roleSelections;
}
