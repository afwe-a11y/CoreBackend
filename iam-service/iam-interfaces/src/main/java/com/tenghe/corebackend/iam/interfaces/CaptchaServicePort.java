package com.tenghe.corebackend.iam.interfaces;

public interface CaptchaServicePort {
  String generateCaptcha(String key);

  boolean validateCaptcha(String key, String captcha);
}
