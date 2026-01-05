package com.tenghe.corebackend.device.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
public class Device {
  private Long id;
  private String name;
  private Long productId;
  private String deviceKey;
  private String deviceSecret;
  private Long gatewayId;
  private Long stationId;
  private DeviceStatus status;
  private Map<String, Object> dynamicAttributes;
  private Instant createdAt;
  private boolean deleted;
}
