package com.tenghe.corebackend.device.model;

public enum ProductType {
  DEVICE,
  GATEWAY;

  public static ProductType fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (ProductType type : values()) {
      if (type.name().equalsIgnoreCase(value.trim())) {
        return type;
      }
    }
    return null;
  }
}
