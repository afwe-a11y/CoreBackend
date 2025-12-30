package com.tenghe.corebackend.device.application.service.result;

import java.time.Instant;

public class DeviceTelemetryResult {
    private String pointIdentifier;
    private Object value;
    private Instant updatedAt;

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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
