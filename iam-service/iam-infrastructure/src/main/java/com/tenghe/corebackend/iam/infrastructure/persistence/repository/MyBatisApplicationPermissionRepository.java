package com.tenghe.corebackend.iam.infrastructure.persistence.repository;

import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.ApplicationPermissionMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.PermissionMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.ApplicationPermissionPo;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.PermissionPo;
import com.tenghe.corebackend.iam.interfaces.ApplicationPermissionRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.iam.model.ApplicationPermission;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MyBatisApplicationPermissionRepository implements ApplicationPermissionRepositoryPort {
  private static final String STATUS_ENABLED = "ENABLED";

  private final ApplicationPermissionMapper applicationPermissionMapper;
  private final PermissionMapper permissionMapper;
  private final IdGeneratorPort idGenerator;

  public MyBatisApplicationPermissionRepository(ApplicationPermissionMapper applicationPermissionMapper,
                                                PermissionMapper permissionMapper,
                                                IdGeneratorPort idGenerator) {
    this.applicationPermissionMapper = applicationPermissionMapper;
    this.permissionMapper = permissionMapper;
    this.idGenerator = idGenerator;
  }

  @Override
  public void saveAll(List<ApplicationPermission> permissions) {
    if (permissions == null || permissions.isEmpty()) {
      return;
    }
    Set<Long> permissionIds = permissions.stream()
        .map(ApplicationPermission::getPermissionId)
        .collect(Collectors.toSet());
    Map<Long, String> codes = mapPermissionIdsToCodes(permissionIds);
    for (ApplicationPermission permission : permissions) {
      String code = codes.get(permission.getPermissionId());
      if (code == null) {
        continue;
      }
      ApplicationPermissionPo po = new ApplicationPermissionPo();
      po.setId(idGenerator.nextId());
      po.setAppId(permission.getAppId());
      po.setPermCode(code);
      po.setStatus(STATUS_ENABLED);
      po.setCreateTime(Instant.now());
      po.setDeleted(0);
      applicationPermissionMapper.insert(po);
    }
  }

  @Override
  public List<ApplicationPermission> listByAppId(Long appId) {
    List<ApplicationPermissionPo> permissions = applicationPermissionMapper.listByAppId(appId);
    if (permissions.isEmpty()) {
      return Collections.emptyList();
    }
    Set<String> codes = permissions.stream()
        .map(ApplicationPermissionPo::getPermCode)
        .collect(Collectors.toSet());
    Map<String, Long> ids = mapPermissionCodesToIds(codes);
    return permissions.stream()
        .map(po -> toModel(po, ids.get(po.getPermCode())))
        .filter(permission -> permission.getPermissionId() != null)
        .collect(Collectors.toList());
  }

  @Override
  public Set<Long> findPermissionIdsByAppId(Long appId) {
    List<ApplicationPermissionPo> permissions = applicationPermissionMapper.listByAppId(appId);
    if (permissions.isEmpty()) {
      return Collections.emptySet();
    }
    Set<String> codes = permissions.stream()
        .map(ApplicationPermissionPo::getPermCode)
        .collect(Collectors.toSet());
    return new HashSet<>(mapPermissionCodesToIds(codes).values());
  }

  @Override
  public void replaceAppPermissions(Long appId, Set<Long> permissionIds) {
    if (permissionIds == null || permissionIds.isEmpty()) {
      applicationPermissionMapper.softDeleteByAppId(appId);
      return;
    }
    Map<Long, String> codes = mapPermissionIdsToCodes(permissionIds);
    Set<String> codeSet = new HashSet<>(codes.values());
    if (codeSet.isEmpty()) {
      applicationPermissionMapper.softDeleteByAppId(appId);
      return;
    }
    applicationPermissionMapper.softDeleteByAppIdAndCodesNotIn(appId, codeSet);
    for (String code : codeSet) {
      int restored = applicationPermissionMapper.restoreByAppIdAndCode(appId, code);
      if (restored == 0) {
        ApplicationPermissionPo po = new ApplicationPermissionPo();
        po.setId(idGenerator.nextId());
        po.setAppId(appId);
        po.setPermCode(code);
        po.setStatus(STATUS_ENABLED);
        po.setCreateTime(Instant.now());
        po.setDeleted(0);
        applicationPermissionMapper.insert(po);
      }
    }
  }

  @Override
  public void softDeleteByAppId(Long appId) {
    applicationPermissionMapper.softDeleteByAppId(appId);
  }

  @Override
  public boolean existsByPermissionIdAndAppId(Long permissionId, Long appId) {
    String code = mapPermissionIdsToCodes(Collections.singleton(permissionId)).get(permissionId);
    if (code == null) {
      return false;
    }
    return applicationPermissionMapper.existsByAppIdAndCode(appId, code) > 0;
  }

  private ApplicationPermission toModel(ApplicationPermissionPo po, Long permissionId) {
    ApplicationPermission permission = new ApplicationPermission();
    permission.setId(po.getId());
    permission.setAppId(po.getAppId());
    permission.setPermissionId(permissionId);
    permission.setCreatedAt(po.getCreateTime());
    permission.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    return permission;
  }

  private Map<Long, String> mapPermissionIdsToCodes(Set<Long> permissionIds) {
    if (permissionIds == null || permissionIds.isEmpty()) {
      return Collections.emptyMap();
    }
    List<PermissionPo> permissions = permissionMapper.findByIds(permissionIds);
    Map<Long, String> map = new HashMap<>();
    for (PermissionPo permission : permissions) {
      map.put(permission.getId(), permission.getPermCode());
    }
    return map;
  }

  private Map<String, Long> mapPermissionCodesToIds(Set<String> codes) {
    if (codes == null || codes.isEmpty()) {
      return Collections.emptyMap();
    }
    List<PermissionPo> permissions = permissionMapper.findByCodes(codes);
    Map<String, Long> map = new HashMap<>();
    for (PermissionPo permission : permissions) {
      map.put(permission.getPermCode(), permission.getId());
    }
    return map;
  }
}
