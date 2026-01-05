package com.tenghe.corebackend.device.interfaces.portdata;

import com.tenghe.corebackend.device.model.enums.EnableStatusEnum;
import com.tenghe.corebackend.device.model.enums.OnlineStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
public class DevicePortData {
  private Long id;
  private String name;
  private Long productId;
  private String deviceKey;
  private String deviceSecret;
  private Long gatewayId;
  private Long stationId;
  private OnlineStatusEnum onlineStatus;
  private EnableStatusEnum enableStatus;
  private Map<String, Object> dynamicAttributes;
  private Instant createdAt;
  private boolean deleted;
}
