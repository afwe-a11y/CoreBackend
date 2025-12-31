package com.tenghe.corebackend.device.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DevicePo {
    private Long id;
    private String name;
    private Long productId;
    private String deviceKey;
    private String deviceSecret;
    private Long gatewayId;
    private Long stationId;
    private String status;
    private Instant lastHeartbeatTime;
    private String dynamicAttributes;
    private Instant createTime;
    private Integer deleted;
}
