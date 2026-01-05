package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class UserPo {
  private Long id;
  private Long homeOrgId;
  private String username;
  private String email;
  private String phone;
  private String passwordHash;
  private String nickname;
  private String status;
  private String attributes;
  private Instant createTime;
  private Integer deleted;
}
