package com.tenghe.corebackend.device.infrastructure.inmemory;

import com.tenghe.corebackend.device.interfaces.TelemetryRepositoryPort;
import com.tenghe.corebackend.device.interfaces.portdata.DeviceTelemetryPortData;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTelemetryRepository implements TelemetryRepositoryPort {
  private final Map<Long, Map<String, DeviceTelemetryPortData>> store = new ConcurrentHashMap<>();

  @Override
  public void saveLatest(DeviceTelemetryPortData telemetry) {
    if (telemetry == null || telemetry.getDeviceId() == null || telemetry.getPointIdentifier() == null) {
      return;
    }
    store.computeIfAbsent(telemetry.getDeviceId(), key -> new ConcurrentHashMap<>())
        .put(telemetry.getPointIdentifier(), telemetry);
  }

  @Override
  public List<DeviceTelemetryPortData> listLatestByDeviceId(Long deviceId) {
    Map<String, DeviceTelemetryPortData> records = store.get(deviceId);
    if (records == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(records.values());
  }
}
