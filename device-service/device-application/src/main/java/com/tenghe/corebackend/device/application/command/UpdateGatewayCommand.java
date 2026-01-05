package com.tenghe.corebackend.device.application.command;

public class UpdateGatewayCommand {
  private Long gatewayId;
  private String name;
  private String sn;
  private Long productId;
  private Long stationId;

  public Long getGatewayId() {
    return gatewayId;
  }

  public void setGatewayId(Long gatewayId) {
    this.gatewayId = gatewayId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSn() {
    return sn;
  }

  public void setSn(String sn) {
    this.sn = sn;
  }

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public Long getStationId() {
    return stationId;
  }

  public void setStationId(Long stationId) {
    this.stationId = stationId;
  }
}
