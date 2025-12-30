package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.OrganizationAppRepositoryPort;
import com.tenghe.corebackend.model.OrganizationApp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrganizationAppRepository implements OrganizationAppRepositoryPort {
    private final Map<Long, Map<Long, OrganizationApp>> store = new ConcurrentHashMap<>();

    @Override
    public List<OrganizationApp> listByOrganizationId(Long organizationId) {
        Map<Long, OrganizationApp> apps = store.get(organizationId);
        if (apps == null) {
            return new ArrayList<>();
        }
        List<OrganizationApp> results = new ArrayList<>();
        for (OrganizationApp app : apps.values()) {
            if (!app.isDeleted()) {
                results.add(app);
            }
        }
        return results;
    }

    @Override
    public Set<Long> findAppIdsByOrganizationIds(Set<Long> organizationIds) {
        Set<Long> appIds = new HashSet<>();
        for (Long orgId : organizationIds) {
            Map<Long, OrganizationApp> apps = store.get(orgId);
            if (apps == null) {
                continue;
            }
            for (OrganizationApp app : apps.values()) {
                if (!app.isDeleted()) {
                    appIds.add(app.getAppId());
                }
            }
        }
        return appIds;
    }

    @Override
    public void replaceOrgApps(Long organizationId, Set<Long> appIds) {
        Map<Long, OrganizationApp> apps = store.computeIfAbsent(organizationId, key -> new ConcurrentHashMap<>());
        Map<Long, OrganizationApp> snapshot = new HashMap<>(apps);
        for (OrganizationApp app : snapshot.values()) {
            if (!appIds.contains(app.getAppId())) {
                app.setDeleted(true);
            }
        }
        for (Long appId : appIds) {
            OrganizationApp mapping = apps.get(appId);
            if (mapping == null) {
                mapping = new OrganizationApp();
                mapping.setOrganizationId(organizationId);
                mapping.setAppId(appId);
                apps.put(appId, mapping);
            }
            mapping.setDeleted(false);
        }
    }
}
