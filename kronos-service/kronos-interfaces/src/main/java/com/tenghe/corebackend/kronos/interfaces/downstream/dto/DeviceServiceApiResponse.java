package com.tenghe.corebackend.kronos.interfaces.downstream.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Device Service API 响应包装（防腐层 DTO）。
 *
 * @param <T> 数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceServiceApiResponse<T> {

  private boolean success;
  private String message;
  private T data;

  /**
   * 判断响应是否成功
   */
  public boolean isOk() {
    return success;
  }
}
