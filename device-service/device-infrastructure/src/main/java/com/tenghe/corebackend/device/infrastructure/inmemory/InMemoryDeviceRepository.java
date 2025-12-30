package com.tenghe.corebackend.device.infrastructure.inmemory;

import com.tenghe.corebackend.device.interfaces.DeviceRepositoryPort;
import com.tenghe.corebackend.device.model.Device;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDeviceRepository implements DeviceRepositoryPort {
    private final Map<Long, Device> store = new ConcurrentHashMap<>();

    @Override
    public Device save(Device device) {
        store.put(device.getId(), device);
        return device;
    }

    @Override
    public Device update(Device device) {
        store.put(device.getId(), device);
        return device;
    }

    @Override
    public Device findById(Long id) {
        return store.get(id);
    }

    @Override
    public Device findByDeviceKey(String deviceKey) {
        if (deviceKey == null) {
            return null;
        }
        for (Device device : store.values()) {
            if (!device.isDeleted() && deviceKey.equals(device.getDeviceKey())) {
                return device;
            }
        }
        return null;
    }

    @Override
    public List<Device> listAll() {
        List<Device> results = new ArrayList<>();
        for (Device device : store.values()) {
            if (!device.isDeleted()) {
                results.add(device);
            }
        }
        return results;
    }

    @Override
    public List<Device> listByProductId(Long productId) {
        List<Device> results = new ArrayList<>();
        if (productId == null) {
            return results;
        }
        for (Device device : store.values()) {
            if (!device.isDeleted() && productId.equals(device.getProductId())) {
                results.add(device);
            }
        }
        return results;
    }

    @Override
    public List<Device> listByGatewayId(Long gatewayId) {
        List<Device> results = new ArrayList<>();
        if (gatewayId == null) {
            return results;
        }
        for (Device device : store.values()) {
            if (!device.isDeleted() && gatewayId.equals(device.getGatewayId())) {
                results.add(device);
            }
        }
        return results;
    }

    @Override
    public List<Device> search(String keyword, Long productId) {
        List<Device> results = new ArrayList<>();
        String normalized = keyword == null ? null : keyword.trim();
        for (Device device : store.values()) {
            if (device.isDeleted()) {
                continue;
            }
            if (productId != null && !productId.equals(device.getProductId())) {
                continue;
            }
            if (normalized == null || normalized.isEmpty()) {
                results.add(device);
                continue;
            }
            if ((device.getName() != null && device.getName().contains(normalized))
                    || (device.getDeviceKey() != null && device.getDeviceKey().contains(normalized))) {
                results.add(device);
            }
        }
        return results;
    }

    @Override
    public long countByProductId(Long productId) {
        return listByProductId(productId).size();
    }
}
