package com.tenghe.corebackend.iam.interfaces;

public interface EmailServicePort {
  void sendVerificationCode(String email, String code);

  void sendInitialPassword(String email, String password);
}
