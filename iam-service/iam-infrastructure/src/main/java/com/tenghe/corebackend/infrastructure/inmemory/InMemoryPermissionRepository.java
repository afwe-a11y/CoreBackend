package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.PermissionRepositoryPort;
import com.tenghe.corebackend.model.Permission;
import com.tenghe.corebackend.model.PermissionStatus;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryPermissionRepository implements PermissionRepositoryPort {
    private final Map<Long, Permission> store = new ConcurrentHashMap<>();

    @Override
    public Permission save(Permission permission) {
        store.put(permission.getId(), permission);
        return permission;
    }

    @Override
    public Permission update(Permission permission) {
        store.put(permission.getId(), permission);
        return permission;
    }

    @Override
    public Permission findById(Long id) {
        if (id == null) {
            return null;
        }
        Permission permission = store.get(id);
        if (permission != null && permission.isDeleted()) {
            return null;
        }
        return permission;
    }

    @Override
    public Permission findByCode(String permissionCode) {
        if (permissionCode == null) {
            return null;
        }
        for (Permission permission : store.values()) {
            if (!permission.isDeleted() && permissionCode.equals(permission.getPermissionCode())) {
                return permission;
            }
        }
        return null;
    }

    @Override
    public List<Permission> listAll() {
        List<Permission> results = new ArrayList<>();
        for (Permission permission : store.values()) {
            if (!permission.isDeleted()) {
                results.add(permission);
            }
        }
        return results;
    }

    @Override
    public List<Permission> findByIds(Set<Long> ids) {
        List<Permission> results = new ArrayList<>();
        if (ids == null) {
            return results;
        }
        for (Long id : ids) {
            Permission permission = store.get(id);
            if (permission != null && !permission.isDeleted()) {
                results.add(permission);
            }
        }
        return results;
    }

    @Override
    public List<Permission> findByParentId(Long parentId) {
        List<Permission> results = new ArrayList<>();
        for (Permission permission : store.values()) {
            if (permission.isDeleted()) {
                continue;
            }
            if ((parentId == null && permission.getParentId() == null)
                    || (parentId != null && parentId.equals(permission.getParentId()))) {
                results.add(permission);
            }
        }
        return results;
    }

    @Override
    public List<Permission> findAllDescendants(Long parentId) {
        List<Permission> results = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        collectDescendants(parentId, results, visited);
        return results;
    }

    private void collectDescendants(Long parentId, List<Permission> results, Set<Long> visited) {
        for (Permission permission : store.values()) {
            if (permission.isDeleted() || visited.contains(permission.getId())) {
                continue;
            }
            if (parentId != null && parentId.equals(permission.getParentId())) {
                visited.add(permission.getId());
                results.add(permission);
                collectDescendants(permission.getId(), results, visited);
            }
        }
    }

    @Override
    public void updateStatusByIds(Set<Long> ids, PermissionStatus status) {
        if (ids == null || status == null) {
            return;
        }
        for (Long id : ids) {
            Permission permission = store.get(id);
            if (permission != null && !permission.isDeleted()) {
                permission.setStatus(status);
            }
        }
    }
}
