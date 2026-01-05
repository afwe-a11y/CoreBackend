package com.tenghe.corebackend.device.api.dto.device;

public class DeviceStatsResponse {
  private Integer totalCount;
  private Integer onlineCount;
  private Integer offlineCount;

  public Integer getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Integer totalCount) {
    this.totalCount = totalCount;
  }

  public Integer getOnlineCount() {
    return onlineCount;
  }

  public void setOnlineCount(Integer onlineCount) {
    this.onlineCount = onlineCount;
  }

  public Integer getOfflineCount() {
    return offlineCount;
  }

  public void setOfflineCount(Integer offlineCount) {
    this.offlineCount = offlineCount;
  }
}
