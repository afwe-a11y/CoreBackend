package com.tenghe.corebackend.iam.infrastructure.inmemory;

import com.tenghe.corebackend.iam.interfaces.ports.EmailCodeService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryEmailCodeService implements EmailCodeService {
  private static final int CODE_TTL_MINUTES = 5;
  private static final int COOLDOWN_SECONDS = 30;

  private final Map<String, CodeEntry> codeStore = new ConcurrentHashMap<>();
  private final Map<String, Instant> lastSentStore = new ConcurrentHashMap<>();
  private final Random random = new Random();

  @Override
  public String generateCode(String email) {
    String code = String.format("%06d", random.nextInt(1000000));
    codeStore.put(email, new CodeEntry(code, Instant.now().plus(CODE_TTL_MINUTES, ChronoUnit.MINUTES)));
    return code;
  }

  @Override
  public boolean validateCode(String email, String code) {
    if (email == null || code == null) {
      return false;
    }
    CodeEntry entry = codeStore.get(email);
    if (entry == null) {
      return false;
    }
    if (Instant.now().isAfter(entry.expiresAt)) {
      codeStore.remove(email);
      return false;
    }
    if (code.equals(entry.code)) {
      codeStore.remove(email);
      return true;
    }
    return false;
  }

  @Override
  public boolean canSendCode(String email) {
    Instant lastSent = lastSentStore.get(email);
    if (lastSent == null) {
      return true;
    }
    return Instant.now().isAfter(lastSent.plus(COOLDOWN_SECONDS, ChronoUnit.SECONDS));
  }

  @Override
  public void markCodeSent(String email) {
    lastSentStore.put(email, Instant.now());
  }

  private static class CodeEntry {
    final String code;
    final Instant expiresAt;

    CodeEntry(String code, Instant expiresAt) {
      this.code = code;
      this.expiresAt = expiresAt;
    }
  }
}
