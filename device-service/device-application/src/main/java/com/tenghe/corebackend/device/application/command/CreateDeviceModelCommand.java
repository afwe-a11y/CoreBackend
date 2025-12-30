package com.tenghe.corebackend.device.application.command;

import java.util.List;

public class CreateDeviceModelCommand {
    private String identifier;
    private String name;
    private String source;
    private Long parentModelId;
    private List<DeviceModelPointCommand> points;

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

    public List<DeviceModelPointCommand> getPoints() {
        return points;
    }

    public void setPoints(List<DeviceModelPointCommand> points) {
        this.points = points;
    }
}
