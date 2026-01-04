package com.tenghe.corebackend.kronos.infrastructure.redis;

import com.tenghe.corebackend.kronos.interfaces.TokenServicePort;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * 基于 Redisson 的 Token 服务实现。
 * 使用 Redis 存储 Token，支持分布式部署和自动过期。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedissonTokenService implements TokenServicePort {

  private static final String TOKEN_PREFIX = "kronos:token:";
  private static final String USER_TOKEN_PREFIX = "kronos:user:token:";
  private static final long TOKEN_EXPIRE_HOURS = 24;

  private final RedissonClient redissonClient;

  @Override
  public String generateToken(Long userId, String username) {
    log.info("生成 Token: userId={}, username={}", userId, username);
    
    String existingToken = getUserToken(userId);
    if (existingToken != null) {
      log.info("用户已有 Token，先删除旧 Token: userId={}", userId);
      invalidateToken(existingToken);
    }
    
    String token = UUID.randomUUID().toString().replace("-", "");
    
    RBucket<Long> tokenBucket = redissonClient.getBucket(TOKEN_PREFIX + token);
    tokenBucket.set(userId, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
    
    RBucket<String> userTokenBucket = redissonClient.getBucket(USER_TOKEN_PREFIX + userId);
    userTokenBucket.set(token, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
    
    log.info("Token 生成成功: userId={}, token={}", userId, token);
    return token;
  }

  @Override
  public Long validateToken(String token) {
    if (token == null || token.isEmpty()) {
      return null;
    }
    
    RBucket<Long> bucket = redissonClient.getBucket(TOKEN_PREFIX + token);
    Long userId = bucket.get();
    
    if (userId != null) {
      log.debug("Token 验证成功: token={}, userId={}", token, userId);
      bucket.expire(TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
      
      RBucket<String> userTokenBucket = redissonClient.getBucket(USER_TOKEN_PREFIX + userId);
      userTokenBucket.expire(TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
    } else {
      log.debug("Token 验证失败: token={}", token);
    }
    
    return userId;
  }

  @Override
  public void invalidateToken(String token) {
    if (token == null || token.isEmpty()) {
      return;
    }
    
    log.info("注销 Token: token={}", token);
    
    RBucket<Long> tokenBucket = redissonClient.getBucket(TOKEN_PREFIX + token);
    Long userId = tokenBucket.get();
    
    tokenBucket.delete();
    
    if (userId != null) {
      RBucket<String> userTokenBucket = redissonClient.getBucket(USER_TOKEN_PREFIX + userId);
      userTokenBucket.delete();
      log.info("Token 注销成功: token={}, userId={}", token, userId);
    }
  }

  @Override
  public void invalidateUserTokens(Long userId) {
    if (userId == null) {
      return;
    }
    
    log.info("注销用户所有 Token: userId={}", userId);
    
    String token = getUserToken(userId);
    if (token != null) {
      RBucket<Long> tokenBucket = redissonClient.getBucket(TOKEN_PREFIX + token);
      tokenBucket.delete();
    }
    
    RBucket<String> userTokenBucket = redissonClient.getBucket(USER_TOKEN_PREFIX + userId);
    userTokenBucket.delete();
    
    log.info("用户 Token 注销成功: userId={}", userId);
  }

  private String getUserToken(Long userId) {
    RBucket<String> bucket = redissonClient.getBucket(USER_TOKEN_PREFIX + userId);
    return bucket.get();
  }
}
