package com.tenghe.corebackend.iam.application;

import com.tenghe.corebackend.iam.application.command.LoginCommand;
import com.tenghe.corebackend.iam.application.command.VerifyCredentialsCommand;
import com.tenghe.corebackend.iam.application.service.result.LoginResult;
import com.tenghe.corebackend.iam.application.service.result.VerifyCredentialsResult;

/**
 * 认证应用服务接口
 * <p>
 * 提供用户登录、登出、会话验证等认证相关功能。
 * </p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>登录标识支持：用户名 / 邮箱 / 手机号 + 密码 + 验证码</li>
 *   <li>锁定策略：连续10次密码错误，锁定账号15分钟</li>
 *   <li>初始密码强制重置：使用系统生成的初始密码登录后，必须强制重置密码</li>
 *   <li>禁用账号拦截：已登录用户被禁用后，任何API调用需返回错误并清除会话</li>
 * </ul>
 */
public interface AuthenticationApplicationService {

  /**
   * 用户登录
   * <p>
   * 验证用户凭证并生成访问令牌。
   * </p>
   *
   * @param command 登录命令，包含登录标识(用户名/邮箱/手机号)、密码、验证码
   * @return 登录结果，包含用户ID、用户名、令牌、是否需要重置密码标志
   * @throws BusinessException 验证码错误、用户名或密码错误、账号已被禁用、账号已被锁定
   */
  LoginResult login(LoginCommand command);

  /**
   * 用户登出
   * <p>
   * 使当前令牌失效，清除用户会话。
   * </p>
   *
   * @param token 访问令牌
   */
  void logout(String token);

  /**
   * 验证会话有效性
   * <p>
   * 检查令牌是否有效，同时验证用户状态（未删除、未禁用）。
   * </p>
   *
   * @param token 访问令牌
   * @return 有效返回用户ID，无效返回null
   */
  Long validateSession(String token);

  /**
   * 生成图形验证码
   *
   * @param key 验证码唯一标识
   * @return 验证码内容（Base64图片或验证码字符串，取决于实现）
   */
  String generateCaptcha(String key);

  /**
   * 验证用户凭证（供 BFF 层调用）
   * <p>
   * 仅验证用户凭证，不生成 Token。Token 由 BFF 层自行管理。
   * </p>
   *
   * @param command 凭证验证命令
   * @return 用户信息（不含 Token）
   * @throws BusinessException 验证码错误、用户名或密码错误、账号已被禁用、账号已被锁定
   */
  VerifyCredentialsResult verifyCredentials(VerifyCredentialsCommand command);

  /**
   * 根据用户 ID 检查用户状态
   * <p>
   * 供 BFF 层验证 Token 后检查用户是否仍然有效（未删除、未禁用）。
   * </p>
   *
   * @param userId 用户 ID
   * @return 用户有效返回 true，否则返回 false
   */
  boolean checkUserStatus(Long userId);
}
