package com.tenghe.corebackend.device.interfaces.portdata;

import com.tenghe.corebackend.device.model.GatewayType;
import com.tenghe.corebackend.device.model.enums.EnableStatusEnum;
import com.tenghe.corebackend.device.model.enums.OnlineStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class GatewayPortData {
  private Long id;
  private String name;
  private GatewayType type;
  private String sn;
  private Long productId;
  private Long stationId;
  private OnlineStatusEnum onlineStatus;
  private EnableStatusEnum enableStatus;
  private Instant createdAt;
  private boolean deleted;
}
