package com.tenghe.corebackend.device.infrastructure.inmemory;

import com.tenghe.corebackend.device.interfaces.DeviceModelRepositoryPort;
import com.tenghe.corebackend.device.model.DeviceModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryDeviceModelRepository implements DeviceModelRepositoryPort {
    private final Map<Long, DeviceModel> store = new ConcurrentHashMap<>();

    @Override
    public DeviceModel save(DeviceModel model) {
        store.put(model.getId(), model);
        return model;
    }

    @Override
    public DeviceModel update(DeviceModel model) {
        store.put(model.getId(), model);
        return model;
    }

    @Override
    public DeviceModel findById(Long id) {
        return store.get(id);
    }

    @Override
    public DeviceModel findByIdentifier(String identifier) {
        if (identifier == null) {
            return null;
        }
        for (DeviceModel model : store.values()) {
            if (!model.isDeleted() && identifier.equals(model.getIdentifier())) {
                return model;
            }
        }
        return null;
    }

    @Override
    public List<DeviceModel> listAll() {
        List<DeviceModel> results = new ArrayList<>();
        for (DeviceModel model : store.values()) {
            if (!model.isDeleted()) {
                results.add(model);
            }
        }
        return results;
    }

    @Override
    public List<DeviceModel> listByParentId(Long parentModelId) {
        List<DeviceModel> results = new ArrayList<>();
        if (parentModelId == null) {
            return results;
        }
        for (DeviceModel model : store.values()) {
            if (!model.isDeleted() && parentModelId.equals(model.getParentModelId())) {
                results.add(model);
            }
        }
        return results;
    }
}
