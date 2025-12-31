package com.tenghe.corebackend.iam.model.enums;

public enum PermissionStatusEnum {
    ENABLED,
    DISABLED;

    public static PermissionStatusEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PermissionStatusEnum status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
