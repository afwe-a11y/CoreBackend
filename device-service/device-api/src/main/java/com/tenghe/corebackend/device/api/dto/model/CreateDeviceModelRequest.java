package com.tenghe.corebackend.device.api.dto.model;

import java.util.List;

public class CreateDeviceModelRequest {
    private String identifier;
    private String name;
    private String source;
    private Long parentModelId;
    private List<DeviceModelPointDto> points;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getParentModelId() {
        return parentModelId;
    }

    public void setParentModelId(Long parentModelId) {
        this.parentModelId = parentModelId;
    }

    public List<DeviceModelPointDto> getPoints() {
        return points;
    }

    public void setPoints(List<DeviceModelPointDto> points) {
        this.points = points;
    }
}
