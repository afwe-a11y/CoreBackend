package com.tenghe.corebackend.model;

public enum PermissionStatus {
    ENABLED,
    DISABLED;

    public static PermissionStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PermissionStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
