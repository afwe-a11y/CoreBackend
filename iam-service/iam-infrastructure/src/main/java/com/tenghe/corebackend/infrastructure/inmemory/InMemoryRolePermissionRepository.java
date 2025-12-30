package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.RolePermissionRepositoryPort;
import com.tenghe.corebackend.interfaces.RoleRepositoryPort;
import com.tenghe.corebackend.model.Role;
import com.tenghe.corebackend.model.RolePermission;
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
public class InMemoryRolePermissionRepository implements RolePermissionRepositoryPort {
    private final Map<Long, RolePermission> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final RoleRepositoryPort roleRepository;

    public InMemoryRolePermissionRepository(RoleRepositoryPort roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void saveAll(List<RolePermission> permissions) {
        if (permissions == null) {
            return;
        }
        for (RolePermission permission : permissions) {
            if (permission.getId() == null) {
                permission.setId(idGenerator.getAndIncrement());
            }
            store.put(permission.getId(), permission);
        }
    }

    @Override
    public List<RolePermission> listByRoleId(Long roleId) {
        List<RolePermission> results = new ArrayList<>();
        for (RolePermission rp : store.values()) {
            if (!rp.isDeleted() && roleId.equals(rp.getRoleId())) {
                results.add(rp);
            }
        }
        return results;
    }

    @Override
    public Set<Long> findPermissionIdsByRoleId(Long roleId) {
        Set<Long> results = new HashSet<>();
        for (RolePermission rp : store.values()) {
            if (!rp.isDeleted() && roleId.equals(rp.getRoleId())) {
                results.add(rp.getPermissionId());
            }
        }
        return results;
    }

    @Override
    public void replaceRolePermissions(Long roleId, Set<Long> permissionIds) {
        for (RolePermission rp : store.values()) {
            if (roleId.equals(rp.getRoleId())) {
                rp.setDeleted(true);
            }
        }
        if (permissionIds == null || permissionIds.isEmpty()) {
            return;
        }
        for (Long permissionId : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setId(idGenerator.getAndIncrement());
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rp.setCreatedAt(Instant.now());
            rp.setDeleted(false);
            store.put(rp.getId(), rp);
        }
    }

    @Override
    public void softDeleteByRoleId(Long roleId) {
        for (RolePermission rp : store.values()) {
            if (roleId.equals(rp.getRoleId())) {
                rp.setDeleted(true);
            }
        }
    }

    @Override
    public boolean existsByPermissionIdAndAppId(Long permissionId, Long appId) {
        List<Role> roles = roleRepository.listByAppId(appId);
        Set<Long> roleIds = new HashSet<>();
        for (Role role : roles) {
            roleIds.add(role.getId());
        }
        for (RolePermission rp : store.values()) {
            if (!rp.isDeleted() && permissionId.equals(rp.getPermissionId()) && roleIds.contains(rp.getRoleId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean existsByRoleId(Long roleId) {
        for (RolePermission rp : store.values()) {
            if (!rp.isDeleted() && roleId.equals(rp.getRoleId())) {
                return true;
            }
        }
        return false;
    }
}
