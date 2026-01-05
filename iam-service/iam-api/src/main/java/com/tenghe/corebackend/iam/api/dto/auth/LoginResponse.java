package com.tenghe.corebackend.iam.api.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginResponse {
  private String userId;
  private String username;
  private String token;
  private boolean requirePasswordReset;
}
