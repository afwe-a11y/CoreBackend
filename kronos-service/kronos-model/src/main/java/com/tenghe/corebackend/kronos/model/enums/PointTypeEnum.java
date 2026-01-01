package com.tenghe.corebackend.kronos.model.enums;

/**
 * 模型点位类型枚举。
 * 区分属性点和遥测点。
 */
public enum PointTypeEnum {
  /**
   * 属性 - 静态配置类数据
   */
  ATTRIBUTE,
  /**
   * 遥测 - 动态采集类数据
   */
  TELEMETRY
}
