package com.tenghe.corebackend.device.application.service.result;

public class CreateDeviceResult {
  private Long id;
  private String deviceSecret;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDeviceSecret() {
    return deviceSecret;
  }

  public void setDeviceSecret(String deviceSecret) {
    this.deviceSecret = deviceSecret;
  }
}
