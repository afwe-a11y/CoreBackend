package com.tenghe.corebackend.iam.application;

import com.tenghe.corebackend.iam.application.command.ResetPasswordCommand;
import com.tenghe.corebackend.iam.application.command.SendEmailCodeCommand;

/**
 * 密码重置应用服务接口
 * <p>
 * 提供邮箱验证码发送和密码重置功能。
 * </p>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>发送验证码：邮箱自动填充用户绑定邮箱，需验证与存储邮箱一致</li>
 *   <li>验证码有效期：5分钟</li>
 *   <li>发送频率限制：成功发送后30秒冷却（防刷）</li>
 *   <li>重置密码需验证：原密码正确、邮箱验证码正确且未过期</li>
 *   <li>新密码规则：长度8-20位，必须包含字母/数字/特殊字符中的至少两种</li>
 *   <li>重置成功后：清除"初始密码"标志，使相关会话/令牌失效</li>
 * </ul>
 */
public interface PasswordResetApplicationService {

  /**
   * 发送邮箱验证码
   * <p>
   * 向用户绑定的邮箱发送验证码，用于密码重置验证。
   * </p>
   *
   * @param command 发送命令，包含用户ID、邮箱（可选，默认使用绑定邮箱）
   * @throws BusinessException 用户不存在、邮箱为空、邮箱与绑定邮箱不匹配、发送过于频繁
   */
  void sendEmailCode(SendEmailCodeCommand command);

  /**
   * 重置密码
   * <p>
   * 验证原密码和邮箱验证码后，更新用户密码。
   * 成功后清除初始密码标志并使所有会话失效。
   * </p>
   *
   * @param command 重置命令，包含用户ID、原密码、新密码、邮箱验证码
   * @throws BusinessException 用户不存在、原密码错误、邮箱验证码错误或已过期、新密码不符合规则
   */
  void resetPassword(ResetPasswordCommand command);
}
