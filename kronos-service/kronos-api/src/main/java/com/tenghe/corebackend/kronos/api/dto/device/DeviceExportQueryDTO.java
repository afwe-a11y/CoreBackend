package com.tenghe.corebackend.kronos.api.dto.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导出查询条件DTO。
 * 导出范围：尊重当前筛选条件（非仅当前页）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExportQueryDTO {

  /**
   * 产品ID
   */
  private Long productId;

  /**
   * 网关ID
   */
  private Long gatewayId;

  /**
   * 电站ID
   */
  private Long stationId;

  /**
   * 设备名称（模糊匹配）
   */
  private String name;

  /**
   * 设备Key（模糊匹配）
   */
  private String deviceKey;

  /**
   * 状态：ONLINE/OFFLINE
   */
  private String status;
}
