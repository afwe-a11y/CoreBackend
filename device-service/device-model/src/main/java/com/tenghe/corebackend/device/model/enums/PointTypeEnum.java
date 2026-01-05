package com.tenghe.corebackend.device.model.enums;

public enum PointTypeEnum {
  ATTRIBUTE,
  MEASUREMENT;

  public static PointTypeEnum fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (PointTypeEnum type : values()) {
      if (type.name().equalsIgnoreCase(value.trim())) {
        return type;
      }
    }
    return null;
  }
}
