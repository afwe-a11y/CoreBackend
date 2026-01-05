package com.tenghe.corebackend.iam.api.dto.role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleMember {
  private String userId;
  private String username;
  private String name;
  private String phone;
  private String email;
}
