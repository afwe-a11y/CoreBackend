package com.tenghe.corebackend.iam.model.enums;

public enum ClientTypeEnum {
  ADMIN,
  APPLICATION;

  public static ClientTypeEnum fromValue(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    if ("管理端".equals(value)) {
      return ADMIN;
    }
    if ("应用端".equals(value)) {
      return APPLICATION;
    }
    try {
      return ClientTypeEnum.valueOf(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
