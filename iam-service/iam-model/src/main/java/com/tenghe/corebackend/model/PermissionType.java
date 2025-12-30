package com.tenghe.corebackend.model;

public enum PermissionType {
    MENU,
    BUTTON;

    public static PermissionType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (PermissionType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
