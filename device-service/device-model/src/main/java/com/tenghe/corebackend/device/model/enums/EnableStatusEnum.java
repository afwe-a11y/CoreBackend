package com.tenghe.corebackend.device.model.enums;

public enum EnableStatusEnum {
  ENABLED,
  DISABLED;

  public static EnableStatusEnum fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (EnableStatusEnum status : values()) {
      if (status.name().equalsIgnoreCase(value.trim())) {
        return status;
      }
    }
    return null;
  }
}
