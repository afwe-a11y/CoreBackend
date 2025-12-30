package com.tenghe.corebackend.device.model;

public enum DeviceDataType {
    INT,
    FLOAT,
    DOUBLE,
    STRING,
    ENUM,
    BOOL,
    DATETIME;

    public static DeviceDataType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DeviceDataType type : values()) {
            if (type.name().equalsIgnoreCase(value.trim())) {
                return type;
            }
        }
        return null;
    }
}
