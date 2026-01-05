package com.tenghe.corebackend.iam.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class UserListItemResult {
  private Long id;
  private String username;
  private String name;
  private String phone;
  private String email;
  private String status;
  private List<String> organizationNames;
  private List<String> roleNames;
  private Instant createdAt;
}
