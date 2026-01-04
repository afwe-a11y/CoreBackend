package com.tenghe.corebackend.kronos.api.common;

/**
 * API 响应常量。
 * 定义统一的响应码和消息，避免魔法值。
 */
public final class ApiConstants {

  private ApiConstants() {
    throw new UnsupportedOperationException("Utility class");
  }

  /**
   * 成功响应码
   */
  public static final String SUCCESS_CODE = "SUCCESS";

  /**
   * 成功响应消息
   */
  public static final String SUCCESS_MESSAGE = "操作成功";

  /**
   * 参数校验失败错误码
   */
  public static final String INVALID_PARAM_CODE = "INVALID_PARAM";

  /**
   * 系统内部错误码
   */
  public static final String INTERNAL_ERROR_CODE = "INTERNAL_ERROR";

  /**
   * 系统内部错误消息
   */
  public static final String INTERNAL_ERROR_MESSAGE = "系统内部错误";

  /**
   * 未授权错误码
   */
  public static final String UNAUTHORIZED_CODE = "UNAUTHORIZED";

  /**
   * 未授权错误消息
   */
  public static final String UNAUTHORIZED_MESSAGE = "未登录或登录已过期";

  /**
   * Token 无效错误码
   */
  public static final String INVALID_TOKEN_CODE = "INVALID_TOKEN";

  /**
   * Token 无效错误消息
   */
  public static final String INVALID_TOKEN_MESSAGE = "Token 无效或已过期";

  /**
   * 用户被禁用错误码
   */
  public static final String USER_DISABLED_CODE = "USER_DISABLED";

  /**
   * 用户被禁用错误消息
   */
  public static final String USER_DISABLED_MESSAGE = "账号已被禁用或删除";

  /**
   * 认证服务异常错误消息
   */
  public static final String AUTH_SERVICE_ERROR_MESSAGE = "认证服务异常";
}
