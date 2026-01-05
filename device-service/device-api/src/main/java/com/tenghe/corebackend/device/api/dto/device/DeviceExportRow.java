package com.tenghe.corebackend.device.api.dto.device;

public class DeviceExportRow {
  private String name;
  private String deviceKey;
  private Long productId;
  private Long gatewayId;
  private Long stationId;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDeviceKey() {
    return deviceKey;
  }

  public void setDeviceKey(String deviceKey) {
    this.deviceKey = deviceKey;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getGatewayId() {
    return gatewayId;
  }

  public void setGatewayId(Long gatewayId) {
    this.gatewayId = gatewayId;
  }

  public Long getStationId() {
    return stationId;
  }

  public void setStationId(Long stationId) {
    this.stationId = stationId;
  }
}
