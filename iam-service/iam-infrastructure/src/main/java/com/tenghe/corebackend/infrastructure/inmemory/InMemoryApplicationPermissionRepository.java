package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.ApplicationPermissionRepositoryPort;
import com.tenghe.corebackend.model.ApplicationPermission;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryApplicationPermissionRepository implements ApplicationPermissionRepositoryPort {
    private final Map<Long, ApplicationPermission> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void saveAll(List<ApplicationPermission> permissions) {
        if (permissions == null) {
            return;
        }
        for (ApplicationPermission permission : permissions) {
            if (permission.getId() == null) {
                permission.setId(idGenerator.getAndIncrement());
            }
            store.put(permission.getId(), permission);
        }
    }

    @Override
    public List<ApplicationPermission> listByAppId(Long appId) {
        List<ApplicationPermission> results = new ArrayList<>();
        for (ApplicationPermission ap : store.values()) {
            if (!ap.isDeleted() && appId.equals(ap.getAppId())) {
                results.add(ap);
            }
        }
        return results;
    }

    @Override
    public Set<Long> findPermissionIdsByAppId(Long appId) {
        Set<Long> results = new HashSet<>();
        for (ApplicationPermission ap : store.values()) {
            if (!ap.isDeleted() && appId.equals(ap.getAppId())) {
                results.add(ap.getPermissionId());
            }
        }
        return results;
    }

    @Override
    public void replaceAppPermissions(Long appId, Set<Long> permissionIds) {
        for (ApplicationPermission ap : store.values()) {
            if (appId.equals(ap.getAppId())) {
                ap.setDeleted(true);
            }
        }
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        for (Long permissionId : permissionIds) {
            ApplicationPermission ap = new ApplicationPermission();
            ap.setId(idGenerator.getAndIncrement());
            ap.setAppId(appId);
            ap.setPermissionId(permissionId);
            ap.setCreatedAt(Instant.now());
            ap.setDeleted(false);
            store.put(ap.getId(), ap);
        }
    }

    @Override
    public void softDeleteByAppId(Long appId) {
        for (ApplicationPermission ap : store.values()) {
            if (appId.equals(ap.getAppId())) {
                ap.setDeleted(true);
            }
        }
    }

    @Override
    public boolean existsByPermissionIdAndAppId(Long permissionId, Long appId) {
        for (ApplicationPermission ap : store.values()) {
            if (!ap.isDeleted() && appId.equals(ap.getAppId()) && permissionId.equals(ap.getPermissionId())) {
                return true;
            }
        }
        return false;
    }
}
