package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备列表项（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceListItem {

  private Long id;
  private String name;
  private Long productId;
  private String deviceKey;
  private Long gatewayId;
  private Long stationId;
  private String status;
}
