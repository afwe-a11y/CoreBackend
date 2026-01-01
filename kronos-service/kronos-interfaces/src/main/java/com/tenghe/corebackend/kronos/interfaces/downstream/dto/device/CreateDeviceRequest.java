package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建设备请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviceRequest {

  private String name;
  private Long productId;
  private String deviceKey;
  private Long gatewayId;
  private Map<String, Object> dynamicAttributes;
}
