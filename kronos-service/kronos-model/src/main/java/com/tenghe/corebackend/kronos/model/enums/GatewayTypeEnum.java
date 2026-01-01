package com.tenghe.corebackend.kronos.model.enums;

/**
 * 网关类型枚举。
 * 区分边缘网关和虚拟网关。
 */
public enum GatewayTypeEnum {
  /**
   * 边缘网关 - 物理设备
   */
  EDGE,
  /**
   * 虚拟网关 - 逻辑网关
   */
  VIRTUAL
}
