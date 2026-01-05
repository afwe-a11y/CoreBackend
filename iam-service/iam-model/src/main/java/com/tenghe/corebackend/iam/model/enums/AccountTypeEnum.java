package com.tenghe.corebackend.iam.model.enums;

public enum AccountTypeEnum {
  MANAGEMENT,
  APPLICATION;

  public static AccountTypeEnum fromValue(String value) {
    if (value == null || value.trim().isEmpty()) {
      return null;
    }
    if ("管理端".equals(value)) {
      return MANAGEMENT;
    }
    if ("应用端".equals(value)) {
      return APPLICATION;
    }
    try {
      return AccountTypeEnum.valueOf(value);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
