package com.tenghe.corebackend.interfaces;

public interface CaptchaServicePort {
    String generateCaptcha(String key);

    boolean validateCaptcha(String key, String captcha);
}
