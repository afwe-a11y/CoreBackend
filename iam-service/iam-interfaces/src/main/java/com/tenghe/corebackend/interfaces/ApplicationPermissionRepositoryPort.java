package com.tenghe.corebackend.interfaces;

import com.tenghe.corebackend.model.ApplicationPermission;
import java.util.List;
import java.util.Set;

public interface ApplicationPermissionRepositoryPort {
    void saveAll(List<ApplicationPermission> permissions);

    List<ApplicationPermission> listByAppId(Long appId);

    Set<Long> findPermissionIdsByAppId(Long appId);

    void replaceAppPermissions(Long appId, Set<Long> permissionIds);

    void softDeleteByAppId(Long appId);

    boolean existsByPermissionIdAndAppId(Long permissionId, Long appId);
}
