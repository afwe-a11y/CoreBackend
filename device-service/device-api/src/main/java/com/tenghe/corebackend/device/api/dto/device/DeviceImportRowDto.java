package com.tenghe.corebackend.device.api.dto.device;

import java.util.Map;

public class DeviceImportRowDto {
  private Long id;
  private String name;
  private Long productId;
  private String deviceKey;
  private Long gatewayId;
  private Map<String, Object> dynamicAttributes;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getDeviceKey() {
    return deviceKey;
  }

  public void setDeviceKey(String deviceKey) {
    this.deviceKey = deviceKey;
  }

  public Long getGatewayId() {
    return gatewayId;
  }

  public void setGatewayId(Long gatewayId) {
    this.gatewayId = gatewayId;
  }

  public Map<String, Object> getDynamicAttributes() {
    return dynamicAttributes;
  }

  public void setDynamicAttributes(Map<String, Object> dynamicAttributes) {
    this.dynamicAttributes = dynamicAttributes;
  }
}
