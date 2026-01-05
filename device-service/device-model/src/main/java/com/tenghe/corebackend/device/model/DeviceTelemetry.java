package com.tenghe.corebackend.device.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class DeviceTelemetry {
  private Long deviceId;
  private String pointIdentifier;
  private Object value;
  private Instant updatedAt;
}
