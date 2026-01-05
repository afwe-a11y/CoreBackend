package com.tenghe.corebackend.device.model.enums;

public enum OnlineStatusEnum {
  ONLINE,
  OFFLINE;

  public static OnlineStatusEnum fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (OnlineStatusEnum status : values()) {
      if (status.name().equalsIgnoreCase(value.trim())) {
        return status;
      }
    }
    return null;
  }
}
