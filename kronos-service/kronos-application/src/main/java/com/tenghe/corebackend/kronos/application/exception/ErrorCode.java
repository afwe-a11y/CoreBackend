package com.tenghe.corebackend.kronos.application.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 错误码枚举。
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  // 通用错误
  DOWNSTREAM_SERVICE_ERROR("DOWNSTREAM_ERROR", "下游服务调用失败"),

  // 设备模型相关
  DEVICE_MODEL_NOT_FOUND("MODEL_NOT_FOUND", "设备模型不存在"),

  // 产品相关
  PRODUCT_NOT_FOUND("PRODUCT_NOT_FOUND", "产品不存在"),

  // 网关相关
  GATEWAY_NOT_FOUND("GATEWAY_NOT_FOUND", "网关不存在"),

  // 设备相关
  DEVICE_NOT_FOUND("DEVICE_NOT_FOUND", "设备不存在");

  private final String code;
  private final String message;
}
