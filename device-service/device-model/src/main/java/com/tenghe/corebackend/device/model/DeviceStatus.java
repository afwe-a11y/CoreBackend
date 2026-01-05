package com.tenghe.corebackend.device.model;

public enum DeviceStatus {
  ONLINE,
  OFFLINE;

  public static DeviceStatus fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (DeviceStatus status : values()) {
      if (status.name().equalsIgnoreCase(value.trim())) {
        return status;
      }
    }
    return null;
  }
}
