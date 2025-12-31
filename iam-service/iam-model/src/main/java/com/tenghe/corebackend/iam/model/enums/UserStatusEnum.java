package com.tenghe.corebackend.iam.model.enums;

public enum UserStatusEnum {
    NORMAL,
    DISABLED;

    public static UserStatusEnum fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        if ("正常".equals(value)) {
            return NORMAL;
        }
        if ("停用".equals(value)) {
            return DISABLED;
        }
        try {
            return UserStatusEnum.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
