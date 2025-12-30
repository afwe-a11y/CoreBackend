package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.model.DeviceTelemetry;
import java.util.List;

public interface TelemetryRepositoryPort {
    void saveLatest(DeviceTelemetry telemetry);

    List<DeviceTelemetry> listLatestByDeviceId(Long deviceId);
}
