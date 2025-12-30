package com.tenghe.corebackend.model;

public enum ApplicationStatus {
    ENABLED,
    DISABLED;

    public static ApplicationStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ApplicationStatus status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
