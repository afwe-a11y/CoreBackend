package com.tenghe.corebackend.kronos.interfaces;

/**
 * Token 服务端口。
 * 定义 Token 生成、验证、失效等操作。
 */
public interface TokenServicePort {

  /**
   * 生成 Token
   *
   * @param userId 用户 ID
   * @param username 用户名
   * @return Token
   */
  String generateToken(Long userId, String username);

  /**
   * 验证 Token
   *
   * @param token Token
   * @return 用户 ID，无效返回 null
   */
  Long validateToken(String token);

  /**
   * 使 Token 失效
   *
   * @param token Token
   */
  void invalidateToken(String token);

  /**
   * 使用户所有 Token 失效
   *
   * @param userId 用户 ID
   */
  void invalidateUserTokens(Long userId);
}
