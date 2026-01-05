package com.tenghe.corebackend.device.model;

public enum DevicePointType {
  ATTRIBUTE,
  TELEMETRY;

  public static DevicePointType fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (DevicePointType type : values()) {
      if (type.name().equalsIgnoreCase(value.trim())) {
        return type;
      }
    }
    return null;
  }
}
