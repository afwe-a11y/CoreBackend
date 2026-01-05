package com.tenghe.corebackend.device.model;

public enum GatewayType {
  EDGE,
  VIRTUAL;

  public static GatewayType fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (GatewayType type : values()) {
      if (type.name().equalsIgnoreCase(value.trim())) {
        return type;
      }
    }
    return null;
  }
}
