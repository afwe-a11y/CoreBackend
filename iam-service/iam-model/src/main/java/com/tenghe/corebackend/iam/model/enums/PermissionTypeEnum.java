package com.tenghe.corebackend.iam.model.enums;

public enum PermissionTypeEnum {
  MENU,
  BUTTON;

  public static PermissionTypeEnum fromValue(String value) {
    if (value == null) {
      return null;
    }
    for (PermissionTypeEnum type : values()) {
      if (type.name().equalsIgnoreCase(value)) {
        return type;
      }
    }
    return null;
  }
}
