package com.tenghe.corebackend.device.model;

public enum DeviceModelSource {
  NEW,
  INHERIT;

  public static DeviceModelSource fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (DeviceModelSource source : values()) {
      if (source.name().equalsIgnoreCase(value.trim())) {
        return source;
      }
    }
    return null;
  }
}
