package com.tenghe.corebackend.device.api.dto.device;

public class DeviceTelemetryItem {
    private String pointIdentifier;
    private Object value;
    private String updatedAt;

    public String getPointIdentifier() {
        return pointIdentifier;
    }

    public void setPointIdentifier(String pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
