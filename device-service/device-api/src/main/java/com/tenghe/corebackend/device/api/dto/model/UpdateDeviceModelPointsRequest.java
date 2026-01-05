package com.tenghe.corebackend.device.api.dto.model;

import java.util.List;

public class UpdateDeviceModelPointsRequest {
  private List<DeviceModelPointDto> points;

  public List<DeviceModelPointDto> getPoints() {
    return points;
  }

  public void setPoints(List<DeviceModelPointDto> points) {
    this.points = points;
  }
}
