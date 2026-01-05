package com.tenghe.corebackend.iam.model;

import com.tenghe.corebackend.iam.model.enums.RoleStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Role {
  private Long id;
  private Long appId;
  private String roleName;
  private String roleCode;
  private String description;
  private RoleStatusEnum status;
  private boolean preset;
  private Instant createdAt;
  private boolean deleted;
}
