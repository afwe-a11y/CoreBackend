package com.tenghe.corebackend.iam.api.dto.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SendEmailCodeRequest {
  private String email;
}
