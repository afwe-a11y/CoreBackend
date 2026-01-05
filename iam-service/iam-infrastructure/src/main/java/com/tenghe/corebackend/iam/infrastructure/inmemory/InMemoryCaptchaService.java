package com.tenghe.corebackend.iam.infrastructure.inmemory;

import com.tenghe.corebackend.iam.interfaces.CaptchaServicePort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryCaptchaService implements CaptchaServicePort {
  private final Map<String, String> store = new ConcurrentHashMap<>();
  private final Random random = new Random();

  @Override
  public String generateCaptcha(String key) {
    String captcha = String.format("%04d", random.nextInt(10000));
    store.put(key, captcha);
    return captcha;
  }

  @Override
  public boolean validateCaptcha(String key, String captcha) {
    if (key == null || captcha == null) {
      return false;
    }
    String stored = store.remove(key);
    return captcha.equals(stored);
  }
}
