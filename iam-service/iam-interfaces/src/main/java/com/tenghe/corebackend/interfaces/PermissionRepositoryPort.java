package com.tenghe.corebackend.interfaces;

import com.tenghe.corebackend.model.Permission;
import com.tenghe.corebackend.model.PermissionStatus;
import java.util.List;
import java.util.Set;

public interface PermissionRepositoryPort {
    Permission save(Permission permission);

    Permission update(Permission permission);

    Permission findById(Long id);

    Permission findByCode(String permissionCode);

    List<Permission> listAll();

    List<Permission> findByIds(Set<Long> ids);

    List<Permission> findByParentId(Long parentId);

    List<Permission> findAllDescendants(Long parentId);

    void updateStatusByIds(Set<Long> ids, PermissionStatus status);
}
