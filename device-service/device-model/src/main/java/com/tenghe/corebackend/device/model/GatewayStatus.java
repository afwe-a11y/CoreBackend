package com.tenghe.corebackend.device.model;

public enum GatewayStatus {
    ONLINE,
    OFFLINE;

    public static GatewayStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (GatewayStatus status : values()) {
            if (status.name().equalsIgnoreCase(value.trim())) {
                return status;
            }
        }
        return null;
    }
}
