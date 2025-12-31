package com.tenghe.corebackend.device.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GatewayPo {
    private Long id;
    private String name;
    private String type;
    private String sn;
    private Long productId;
    private Long stationId;
    private String status;
    private Integer enabled;
    private Instant lastHeartbeatTime;
    private Instant createTime;
    private Integer deleted;
}
