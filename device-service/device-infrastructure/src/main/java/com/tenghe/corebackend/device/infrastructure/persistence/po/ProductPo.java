package com.tenghe.corebackend.device.infrastructure.persistence.po;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ProductPo {
  private Long id;
  private String productType;
  private String name;
  private String description;
  private String productKey;
  private String productSecret;
  private Long deviceModelId;
  private String accessMode;
  private String protocolMapping;
  private String status;
  private Instant createTime;
  private Integer deleted;
}
