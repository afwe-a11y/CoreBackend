package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新设备请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeviceRequest {

  private String name;
  private Long gatewayId;
  private Map<String, Object> dynamicAttributes;
}
