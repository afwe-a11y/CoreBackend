package com.tenghe.corebackend.kronos.model.enums;

/**
 * 产品接入方式枚举。
 * 定义设备与平台的通信协议。
 */
public enum AccessModeEnum {
  /**
   * 通用MQTT接入
   */
  GENERAL_MQTT,
  /**
   * 自定义协议
   */
  CUSTOM
}
