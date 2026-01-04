package com.tenghe.corebackend.kronos.interfaces.downstream.dto.iam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IAM 服务 API 响应封装。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IamApiResponse<T> {

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
   * 是否成功
   */
  public boolean isOk() {
    return "200".equals(code) || "0".equals(code);
  }

  public static <T> IamApiResponse<T> ok(T data) {
    return new IamApiResponse<>("200", "success", data);
  }

  public static <T> IamApiResponse<T> error(String code, String message) {
    return new IamApiResponse<>(code, message, null);
  }
}
