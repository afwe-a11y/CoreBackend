package com.tenghe.corebackend.iam.application.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 凭证验证命令。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCredentialsCommand {

  private String identifier;
  private String password;
  private String captcha;
  private String captchaKey;
}
