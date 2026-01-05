package com.tenghe.corebackend.device.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
public class Product {
  private Long id;
  private ProductType productType;
  private String name;
  private String productKey;
  private String productSecret;
  private Long deviceModelId;
  private String accessMode;
  private String description;
  private Map<String, String> protocolMapping;
  private Instant createdAt;
  private boolean deleted;
}
