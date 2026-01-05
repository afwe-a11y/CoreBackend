package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.interfaces.portdata.DeviceTelemetryPortData;

import java.util.List;

public interface TelemetryRepositoryPort {
  void saveLatest(DeviceTelemetryPortData telemetry);

  List<DeviceTelemetryPortData> listLatestByDeviceId(Long deviceId);
}
