package com.tenghe.corebackend.iam.model;

import com.tenghe.corebackend.iam.model.enums.AccountTypeEnum;
import com.tenghe.corebackend.iam.model.enums.UserStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class User {
  private Long id;
  private String username;
  private String name;
  private String phone;
  private String email;
  private String password;
  private boolean initialPasswordFlag;
  private int failedLoginAttempts;
  private Instant lockedUntil;
  private UserStatusEnum status;
  private AccountTypeEnum accountType;
  private Long primaryOrgId;
  private Instant createdAt;
  private boolean deleted;
}
