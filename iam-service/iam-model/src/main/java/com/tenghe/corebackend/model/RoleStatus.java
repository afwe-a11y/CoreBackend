package com.tenghe.corebackend.model;

public enum RoleStatus {
    ENABLED,
    DISABLED;

    public static RoleStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RoleStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
