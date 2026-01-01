package com.tenghe.corebackend.kronos.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一API响应封装。
 *
 * @param <T> 响应数据类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  /**
   * 响应码
   */
  private String code;

  /**
   * 响应消息
   */
  private String message;

  /**
   * 响应数据
   */
  private T data;

  /**
   * 链路追踪ID
   */
  private String traceId;

  /**
   * 下游错误详情
   */
  private ErrorDetail details;

  /**
   * 成功响应
   */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .code("SUCCESS")
        .message("操作成功")
        .data(data)
        .build();
  }

  /**
   * 成功响应（无数据）
   */
  public static <T> ApiResponse<T> success() {
    return success(null);
  }

  /**
   * 失败响应
   */
  public static <T> ApiResponse<T> error(String code, String message) {
    return ApiResponse.<T>builder()
        .code(code)
        .message(message)
        .build();
  }

  /**
   * 失败响应（带详情）
   */
  public static <T> ApiResponse<T> error(String code, String message, ErrorDetail details) {
    return ApiResponse.<T>builder()
        .code(code)
        .message(message)
        .details(details)
        .build();
  }

  /**
   * 错误详情
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorDetail {
    /**
     * 下游错误码
     */
    private String downstreamCode;

    /**
     * 下游错误消息
     */
    private String downstreamMessage;
  }
}
