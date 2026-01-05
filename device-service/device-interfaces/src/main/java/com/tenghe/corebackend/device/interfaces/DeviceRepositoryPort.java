package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.interfaces.portdata.DevicePortData;

import java.util.List;

public interface DeviceRepositoryPort {
  DevicePortData save(DevicePortData device);

  DevicePortData update(DevicePortData device);

  DevicePortData findById(Long id);

  DevicePortData findByDeviceKey(String deviceKey);

  List<DevicePortData> listAll();

  List<DevicePortData> listByProductId(Long productId);

  List<DevicePortData> listByGatewayId(Long gatewayId);

  List<DevicePortData> search(String keyword, Long productId);

  long countByProductId(Long productId);
}
