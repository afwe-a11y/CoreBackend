package com.tenghe.corebackend.iam.interfaces;

public interface EmailCodeServicePort {
  String generateCode(String email);

  boolean validateCode(String email, String code);

  boolean canSendCode(String email);

  void markCodeSent(String email);
}
