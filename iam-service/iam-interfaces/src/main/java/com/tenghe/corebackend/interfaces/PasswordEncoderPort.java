package com.tenghe.corebackend.interfaces;

public interface PasswordEncoderPort {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);

    String generateInitialPassword();
}
