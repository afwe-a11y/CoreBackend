package com.tenghe.corebackend.iam.interfaces;

import com.tenghe.corebackend.iam.model.RolePermission;

import java.util.List;
import java.util.Set;

public interface RolePermissionRepositoryPort {
  void saveAll(List<RolePermission> permissions);

  List<RolePermission> listByRoleId(Long roleId);

  Set<Long> findPermissionIdsByRoleId(Long roleId);

  void replaceRolePermissions(Long roleId, Set<Long> permissionIds);

  void softDeleteByRoleId(Long roleId);

  boolean existsByPermissionIdAndAppId(Long permissionId, Long appId);

  boolean existsByRoleId(Long roleId);
}
