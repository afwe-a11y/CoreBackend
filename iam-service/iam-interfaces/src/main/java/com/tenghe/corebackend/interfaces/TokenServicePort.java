package com.tenghe.corebackend.interfaces;

public interface TokenServicePort {
    String generateToken(Long userId, String username);

    Long validateToken(String token);

    void invalidateToken(String token);

    void invalidateUserTokens(Long userId);
}
