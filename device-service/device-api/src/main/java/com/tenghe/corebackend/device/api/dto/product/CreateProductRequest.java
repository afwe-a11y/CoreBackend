package com.tenghe.corebackend.device.api.dto.product;

import java.util.Map;

public class CreateProductRequest {
  private String productType;
  private String name;
  private Long deviceModelId;
  private String accessMode;
  private String description;
  private Map<String, String> protocolMapping;

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getDeviceModelId() {
    return deviceModelId;
  }

  public void setDeviceModelId(Long deviceModelId) {
    this.deviceModelId = deviceModelId;
  }

  public String getAccessMode() {
    return accessMode;
  }

  public void setAccessMode(String accessMode) {
    this.accessMode = accessMode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, String> getProtocolMapping() {
    return protocolMapping;
  }

  public void setProtocolMapping(Map<String, String> protocolMapping) {
    this.protocolMapping = protocolMapping;
  }
}
