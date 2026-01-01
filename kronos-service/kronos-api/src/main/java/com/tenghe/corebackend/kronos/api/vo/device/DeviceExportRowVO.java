package com.tenghe.corebackend.kronos.api.vo.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导出行 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExportRowVO {

  /**
   * 设备名称
   */
  private String name;

  /**
   * 设备Key
   */
  private String deviceKey;

  /**
   * 产品ID
   */
  private String productId;

  /**
   * 网关ID
   */
  private String gatewayId;

  /**
   * 电站ID
   */
  private String stationId;
}
