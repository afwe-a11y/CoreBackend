package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.model.DeviceModel;

import java.util.List;

public interface DeviceModelRepositoryPort {
  DeviceModel save(DeviceModel model);

  DeviceModel update(DeviceModel model);

  DeviceModel findById(Long id);

  DeviceModel findByIdentifier(String identifier);

  List<DeviceModel> listAll();

  List<DeviceModel> listByParentId(Long parentModelId);
}
