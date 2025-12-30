package com.tenghe.corebackend.device.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Gateway {
    private Long id;
    private String name;
    private GatewayType type;
    private String sn;
    private Long productId;
    private Long stationId;
    private GatewayStatus status;
    private boolean enabled;
    private Instant createdAt;
    private boolean deleted;
}
