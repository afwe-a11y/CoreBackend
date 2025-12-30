package com.tenghe.corebackend.device.model;

import java.time.Instant;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

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
