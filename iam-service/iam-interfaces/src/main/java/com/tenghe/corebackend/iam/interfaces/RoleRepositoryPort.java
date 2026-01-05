package com.tenghe.corebackend.iam.interfaces;

import com.tenghe.corebackend.iam.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleRepositoryPort {
  Role save(Role role);

  Role update(Role role);

  Role findById(Long id);

  Role findByCode(String roleCode);

  Role findByNameAndAppId(String roleName, Long appId);

  List<Role> listAll();

  List<Role> listByAppId(Long appId);

  List<Role> findByIds(Set<Long> ids);

  List<Role> findByCodes(Set<String> roleCodes);

  int countByAppId(Long appId);

  void softDeleteById(Long id);
}
