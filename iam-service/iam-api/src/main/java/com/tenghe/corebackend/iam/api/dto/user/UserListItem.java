package com.tenghe.corebackend.iam.api.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserListItem {
  private String id;
  private String username;
  private String name;
  private String phone;
  private String email;
  private String status;
  private List<String> organizationNames;
  private List<String> roleNames;
  private String createdDate;
}
