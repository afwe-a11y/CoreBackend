package com.tenghe.corebackend.device.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class DeviceModelPo {
  private Long id;
  private String identifier;
  private String name;
  private String source;
  private Long parentModelId;
  private String description;
  private Instant createTime;
  private Integer deleted;
}
