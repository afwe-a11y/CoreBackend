package com.tenghe.corebackend.kronos.controller.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tenghe.corebackend.kronos.api.common.ApiConstants;
import com.tenghe.corebackend.kronos.api.common.ApiResponse;
import com.tenghe.corebackend.kronos.interfaces.TokenServicePort;
import com.tenghe.corebackend.kronos.interfaces.downstream.IamServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam.IamApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器。
 * 拦截所有请求，验证 Token 有效性。
 * 
 * Token 存储在本地 Redis（由 kronos-service 管理），
 * 验证后可选择调用 iam-service 检查用户状态。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

  private final TokenServicePort tokenService;
  private final IamServiceClient iamServiceClient;
  private final ObjectMapper objectMapper;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) 
      throws Exception {
    
    String uri = request.getRequestURI();
    log.debug("认证拦截: method={} uri={}", request.getMethod(), uri);
    
    if (uri.startsWith("/api/v1/auth/") || uri.equals("/api/v1/health")) {
      log.debug("跳过认证检查: uri={}", uri);
      return true;
    }
    
    String authorization = request.getHeader("Authorization");
    if (authorization == null || authorization.isEmpty()) {
      log.warn("缺少 Authorization header: uri={}", uri);
      writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
          ApiConstants.UNAUTHORIZED_CODE, ApiConstants.UNAUTHORIZED_MESSAGE);
      return false;
    }
    
    String token = authorization;
    if (authorization.startsWith("Bearer ")) {
      token = authorization.substring(7);
    }
    
    try {
      Long userId = tokenService.validateToken(token);
      if (userId == null) {
        log.warn("Token 验证失败: uri={}", uri);
        writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
            ApiConstants.INVALID_TOKEN_CODE, ApiConstants.INVALID_TOKEN_MESSAGE);
        return false;
      }
      
      IamApiResponse<Boolean> statusResponse = iamServiceClient.checkUserStatus(userId);
      if (!statusResponse.isOk() || !Boolean.TRUE.equals(statusResponse.getData())) {
        log.warn("用户状态无效: uri={}, userId={}", uri, userId);
        tokenService.invalidateToken(token);
        writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, 
            ApiConstants.USER_DISABLED_CODE, ApiConstants.USER_DISABLED_MESSAGE);
        return false;
      }
      
      request.setAttribute("userId", userId);
      log.debug("Token 验证成功: uri={}, userId={}", uri, userId);
      return true;
      
    } catch (Exception e) {
      log.error("Token 验证异常: uri={}", uri, e);
      writeErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
          ApiConstants.INTERNAL_ERROR_CODE, ApiConstants.AUTH_SERVICE_ERROR_MESSAGE);
      return false;
    }
  }

  /**
   * 写入错误响应
   *
   * @param response HTTP 响应
   * @param status   HTTP 状态码
   * @param code     错误码
   * @param message  错误消息
   */
  private void writeErrorResponse(HttpServletResponse response, int status, String code, String message) {
    response.setStatus(status);
    response.setContentType("application/json;charset=UTF-8");
    try {
      ApiResponse<Void> errorResponse = ApiResponse.error(code, message);
      response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    } catch (IOException e) {
      log.error("写入错误响应失败", e);
    }
  }
}
