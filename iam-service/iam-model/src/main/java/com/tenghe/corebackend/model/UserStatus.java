package com.tenghe.corebackend.model;

public enum UserStatus {
    NORMAL,
    DISABLED;

    public static UserStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        if ("正常".equals(value)) {
            return NORMAL;
        }
        if ("停用".equals(value)) {
            return DISABLED;
        }
        return UserStatus.valueOf(value);
    }
}
