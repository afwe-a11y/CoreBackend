package com.tenghe.corebackend.kronos.api.vo.device;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导出数据VO。
 * 文件名格式: {Product}_{Description}_{Timestamp}.xlsx
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExportVO {

  /**
   * 设备ID
   */
  private String id;

  /**
   * 设备名称
   */
  private String name;

  /**
   * 设备Key
   */
  private String deviceKey;

  /**
   * 产品名称
   */
  private String productName;

  /**
   * 网关名称
   */
  private String gatewayName;

  /**
   * 网关序列号
   */
  private String gatewaySn;

  /**
   * 电站名称
   */
  private String stationName;

  /**
   * 状态
   */
  private String status;

  /**
   * 动态属性
   */
  private Map<String, Object> dynamicAttributes;

  /**
   * 描述
   */
  private String description;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;
}
