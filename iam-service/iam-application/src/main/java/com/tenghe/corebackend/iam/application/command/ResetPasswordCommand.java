package com.tenghe.corebackend.iam.application.command;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordCommand {
  private Long userId;
  private String oldPassword;
  private String newPassword;
  private String emailCode;
}
