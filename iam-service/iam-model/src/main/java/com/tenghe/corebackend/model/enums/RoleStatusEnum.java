package com.tenghe.corebackend.model.enums;

public enum RoleStatusEnum {
    ENABLED,
    DISABLED;

    public static RoleStatusEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (RoleStatusEnum status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
