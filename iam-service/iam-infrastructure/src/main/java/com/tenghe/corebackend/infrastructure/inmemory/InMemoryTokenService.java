package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.TokenServicePort;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class InMemoryTokenService implements TokenServicePort {
    private final Map<String, Long> tokenToUser = new ConcurrentHashMap<>();
    private final Map<Long, String> userToToken = new ConcurrentHashMap<>();

    @Override
    public String generateToken(Long userId, String username) {
        String existingToken = userToToken.get(userId);
        if (existingToken != null) {
            tokenToUser.remove(existingToken);
        }
        String token = UUID.randomUUID().toString();
        tokenToUser.put(token, userId);
        userToToken.put(userId, token);
        return token;
    }

    @Override
    public Long validateToken(String token) {
        if (token == null) {
            return null;
        }
        return tokenToUser.get(token);
    }

    @Override
    public void invalidateToken(String token) {
        if (token == null) {
            return;
        }
        Long userId = tokenToUser.remove(token);
        if (userId != null) {
            userToToken.remove(userId);
        }
    }

    @Override
    public void invalidateUserTokens(Long userId) {
        if (userId == null) {
            return;
        }
        String token = userToToken.remove(userId);
        if (token != null) {
            tokenToUser.remove(token);
        }
    }
}
