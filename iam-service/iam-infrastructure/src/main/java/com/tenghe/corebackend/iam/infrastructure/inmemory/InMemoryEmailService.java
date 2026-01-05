package com.tenghe.corebackend.iam.infrastructure.inmemory;

import com.tenghe.corebackend.iam.interfaces.EmailServicePort;
import org.springframework.stereotype.Service;

@Service
public class InMemoryEmailService implements EmailServicePort {

  @Override
  public void sendVerificationCode(String email, String code) {
    System.out.println("[EMAIL] Verification code for " + email + ": " + code);
  }

  @Override
  public void sendInitialPassword(String email, String password) {
    System.out.println("[EMAIL] Initial password for " + email + ": " + password);
  }
}
