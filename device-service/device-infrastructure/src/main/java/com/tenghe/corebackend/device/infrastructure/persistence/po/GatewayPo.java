package com.tenghe.corebackend.device.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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
