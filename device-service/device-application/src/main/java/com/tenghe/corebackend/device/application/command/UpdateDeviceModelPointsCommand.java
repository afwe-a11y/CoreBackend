package com.tenghe.corebackend.device.application.command;

import java.util.List;

public class UpdateDeviceModelPointsCommand {
    private Long modelId;
    private List<DeviceModelPointCommand> points;

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public List<DeviceModelPointCommand> getPoints() {
        return points;
    }

    public void setPoints(List<DeviceModelPointCommand> points) {
        this.points = points;
    }
}
