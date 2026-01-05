package com.tenghe.corebackend.device.interfaces.ports;

import com.tenghe.corebackend.device.interfaces.portdata.DeviceModelPortData;

import java.util.List;

public interface DeviceModelRepository {
  DeviceModelPortData save(DeviceModelPortData model);

  DeviceModelPortData update(DeviceModelPortData model);

  DeviceModelPortData findById(Long id);

  DeviceModelPortData findByIdentifier(String identifier);

  List<DeviceModelPortData> listAll();

  List<DeviceModelPortData> listByParentId(Long parentModelId);
}
