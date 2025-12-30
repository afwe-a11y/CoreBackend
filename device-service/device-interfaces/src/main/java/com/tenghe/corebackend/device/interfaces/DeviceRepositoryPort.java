package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.model.Device;
import java.util.List;

public interface DeviceRepositoryPort {
    Device save(Device device);

    Device update(Device device);

    Device findById(Long id);

    Device findByDeviceKey(String deviceKey);

    List<Device> listAll();

    List<Device> listByProductId(Long productId);

    List<Device> listByGatewayId(Long gatewayId);

    List<Device> search(String keyword, Long productId);

    long countByProductId(Long productId);
}
