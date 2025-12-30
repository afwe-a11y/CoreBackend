package com.tenghe.corebackend.device.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceTelemetry {
    private Long deviceId;
    private String pointIdentifier;
    private Object value;
    private Instant updatedAt;
}
