package com.tenghe.corebackend.device.interfaces.ports;

import com.tenghe.corebackend.device.interfaces.portdata.DeviceTelemetryPortData;

import java.util.List;

public interface TelemetryRepository {
  void saveLatest(DeviceTelemetryPortData telemetry);

  List<DeviceTelemetryPortData> listLatestByDeviceId(Long deviceId);
}
