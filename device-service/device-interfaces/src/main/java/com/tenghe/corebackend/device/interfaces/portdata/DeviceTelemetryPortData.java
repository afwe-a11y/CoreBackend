package com.tenghe.corebackend.device.interfaces.portdata;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class DeviceTelemetryPortData {
  private Long deviceId;
  private String pointIdentifier;
  private Object value;
  private Instant updatedAt;
}
