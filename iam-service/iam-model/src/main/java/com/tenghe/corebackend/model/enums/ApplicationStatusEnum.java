package com.tenghe.corebackend.model.enums;

public enum ApplicationStatusEnum {
    ENABLED,
    DISABLED;

    public static ApplicationStatusEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ApplicationStatusEnum status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
