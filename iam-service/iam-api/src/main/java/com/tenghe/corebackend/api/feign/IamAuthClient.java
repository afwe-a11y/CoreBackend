package com.tenghe.corebackend.api.feign;

import com.tenghe.corebackend.api.dto.auth.CaptchaResponse;
import com.tenghe.corebackend.api.dto.auth.LoginRequest;
import com.tenghe.corebackend.api.dto.auth.LoginResponse;
import com.tenghe.corebackend.api.dto.common.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * IAM 认证服务 Feign Client
 * <p>
 * 供上层微服务调用 iam-service 的认证相关功能。
 * 上层微服务通过引入 iam-api 依赖并注入此接口即可调用。
 * </p>
 * 
 * <h3>使用方式</h3>
 * <pre>
 * &#64;Autowired
 * private IamAuthClient iamAuthClient;
 * 
 * // 调用登录
 * ApiResponse&lt;LoginResponse&gt; result = iamAuthClient.login(request);
 * </pre>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>登录支持：用户名/邮箱/手机号 + 密码 + 验证码</li>
 *   <li>锁定策略：连续10次密码错误，锁定账号15分钟</li>
 *   <li>初始密码：使用初始密码登录后需强制重置</li>
 * </ul>
 */
@FeignClient(name = "iam-service", path = "/api/auth", contextId = "iamAuthClient")
public interface IamAuthClient {

    /**
     * 获取图形验证码
     * 
     * @return 验证码响应，包含验证码key和验证码内容
     */
    @GetMapping("/captcha")
    ApiResponse<CaptchaResponse> getCaptcha();

    /**
     * 用户登录
     * <p>
     * 验证用户凭证并生成访问令牌。
     * </p>
     * 
     * @param request 登录请求，包含登录标识、密码、验证码
     * @return 登录响应，包含用户ID、用户名、令牌、是否需要重置密码
     */
    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginRequest request);

    /**
     * 用户登出
     * <p>
     * 使当前令牌失效，清除用户会话。
     * </p>
     * 
     * @param token Bearer令牌（格式：Bearer xxx）
     * @return 空响应
     */
    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestHeader("Authorization") String token);

    /**
     * 验证会话有效性
     * <p>
     * 检查令牌是否有效，同时验证用户状态（未删除、未禁用）。
     * </p>
     * 
     * @param token Bearer令牌（格式：Bearer xxx）
     * @return true表示会话有效，false表示无效
     */
    @GetMapping("/validate")
    ApiResponse<Boolean> validateSession(@RequestHeader("Authorization") String token);
}
