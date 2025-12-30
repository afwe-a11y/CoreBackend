package com.tenghe.corebackend.device.infrastructure.inmemory;

import com.tenghe.corebackend.device.interfaces.GatewayRepositoryPort;
import com.tenghe.corebackend.device.model.Gateway;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryGatewayRepository implements GatewayRepositoryPort {
    private final Map<Long, Gateway> store = new ConcurrentHashMap<>();

    @Override
    public Gateway save(Gateway gateway) {
        store.put(gateway.getId(), gateway);
        return gateway;
    }

    @Override
    public Gateway update(Gateway gateway) {
        store.put(gateway.getId(), gateway);
        return gateway;
    }

    @Override
    public Gateway findById(Long id) {
        return store.get(id);
    }

    @Override
    public Gateway findBySn(String sn) {
        if (sn == null) {
            return null;
        }
        for (Gateway gateway : store.values()) {
            if (!gateway.isDeleted() && sn.equals(gateway.getSn())) {
                return gateway;
            }
        }
        return null;
    }

    @Override
    public List<Gateway> listAll() {
        List<Gateway> results = new ArrayList<>();
        for (Gateway gateway : store.values()) {
            if (!gateway.isDeleted()) {
                results.add(gateway);
            }
        }
        return results;
    }

    @Override
    public List<Gateway> searchByNameOrSn(String keyword) {
        List<Gateway> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }
        String normalized = keyword.trim();
        for (Gateway gateway : store.values()) {
            if (gateway.isDeleted()) {
                continue;
            }
            if ((gateway.getName() != null && gateway.getName().contains(normalized))
                    || (gateway.getSn() != null && gateway.getSn().contains(normalized))) {
                results.add(gateway);
            }
        }
        return results;
    }
}
