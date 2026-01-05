package com.tenghe.corebackend.iam.infrastructure.persistence.repository;

import com.tenghe.corebackend.iam.infrastructure.persistence.json.JsonCodec;
import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.RoleGrantMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.RoleMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.RolePo;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.UserRolePo;
import com.tenghe.corebackend.iam.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.iam.interfaces.RoleGrantRepositoryPort;
import com.tenghe.corebackend.iam.model.RoleGrant;
import com.tenghe.corebackend.iam.model.enums.RoleCategoryEnum;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MyBatisRoleGrantRepository implements RoleGrantRepositoryPort {
  private static final String KEY_ROLE_CATEGORY = "roleCategory";

  private final RoleGrantMapper roleGrantMapper;
  private final RoleMapper roleMapper;
  private final IdGeneratorPort idGenerator;
  private final JsonCodec jsonCodec;

  public MyBatisRoleGrantRepository(RoleGrantMapper roleGrantMapper,
                                    RoleMapper roleMapper,
                                    IdGeneratorPort idGenerator,
                                    JsonCodec jsonCodec) {
    this.roleGrantMapper = roleGrantMapper;
    this.roleMapper = roleMapper;
    this.idGenerator = idGenerator;
    this.jsonCodec = jsonCodec;
  }

  @Override
  public void saveAll(List<RoleGrant> grants) {
    if (grants == null || grants.isEmpty()) {
      return;
    }
    for (RoleGrant grant : grants) {
      UserRolePo po = new UserRolePo();
      po.setId(grant.getId() == null ? idGenerator.nextId() : grant.getId());
      po.setOrgId(grant.getOrganizationId());
      po.setUserId(grant.getUserId());
      po.setAppId(grant.getAppId());
      po.setRoleId(grant.getRoleId());
      po.setAssetScope(buildAssetScope(grant));
      po.setCreateTime(grant.getCreatedAt() == null ? Instant.now() : grant.getCreatedAt());
      po.setDeleted(grant.isDeleted() ? 1 : 0);
      roleGrantMapper.insert(po);
    }
  }

  @Override
  public List<RoleGrant> listByUserIdAndOrganizationId(Long userId, Long organizationId) {
    return roleGrantMapper.listByUserIdAndOrgId(userId, organizationId).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public void softDeleteByOrganizationIdAndAppIds(Long organizationId, Set<Long> appIds) {
    if (appIds == null || appIds.isEmpty()) {
      return;
    }
    roleGrantMapper.softDeleteByOrgIdAndAppIds(organizationId, appIds);
  }

  @Override
  public void softDeleteByOrganizationId(Long organizationId) {
    roleGrantMapper.softDeleteByOrgId(organizationId);
  }

  @Override
  public void softDeleteByUserIdAndOrganizationId(Long userId, Long organizationId) {
    roleGrantMapper.softDeleteByUserIdAndOrgId(userId, organizationId);
  }

  @Override
  public void softDeleteByUserIdAndOrganizationIdAndRoleCode(Long userId, Long organizationId, String roleCode) {
    RolePo role = roleMapper.findByCode(roleCode);
    if (role == null) {
      return;
    }
    roleGrantMapper.softDeleteByUserIdAndOrgIdAndRoleId(userId, organizationId, role.getId());
  }

  @Override
  public void updateRoleCategoryByUserAndOrganization(Long userId, Long organizationId, RoleCategoryEnum roleCategory) {
    Map<String, Object> payload = new LinkedHashMap<>();
    if (roleCategory != null) {
      payload.put(KEY_ROLE_CATEGORY, roleCategory.name());
    }
    String assetScope = payload.isEmpty() ? null : jsonCodec.writeValue(payload);
    roleGrantMapper.updateAssetScopeByUserIdAndOrgId(userId, organizationId, assetScope);
  }

  @Override
  public boolean existsByRoleId(Long roleId) {
    return roleGrantMapper.existsByRoleId(roleId) > 0;
  }

  @Override
  public boolean existsByRoleCode(String roleCode) {
    if (roleCode == null) {
      return false;
    }
    return roleGrantMapper.existsByRoleCode(roleCode) > 0;
  }

  @Override
  public List<RoleGrant> listByRoleId(Long roleId) {
    return roleGrantMapper.listByRoleId(roleId).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<RoleGrant> listByRoleIdAndOrganizationId(Long roleId, Long organizationId) {
    return roleGrantMapper.listByRoleIdAndOrgId(roleId, organizationId).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public void softDeleteByRoleId(Long roleId) {
    roleGrantMapper.softDeleteByRoleId(roleId);
  }

  private RoleGrant toModel(UserRolePo po) {
    RoleGrant grant = new RoleGrant();
    grant.setId(po.getId());
    grant.setOrganizationId(po.getOrgId());
    grant.setUserId(po.getUserId());
    grant.setAppId(po.getAppId());
    grant.setRoleId(po.getRoleId());
    grant.setRoleCode(po.getRoleCode());
    grant.setCreatedAt(po.getCreateTime());
    grant.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    grant.setRoleCategory(parseRoleCategory(po.getAssetScope()));
    return grant;
  }

  private RoleCategoryEnum parseRoleCategory(String assetScope) {
    if (assetScope == null || assetScope.isBlank()) {
      return null;
    }
    Map<String, Object> payload = jsonCodec.readMap(assetScope);
    Object value = payload.get(KEY_ROLE_CATEGORY);
    if (value == null) {
      return null;
    }
    try {
      return RoleCategoryEnum.valueOf(value.toString());
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }

  private String buildAssetScope(RoleGrant grant) {
    if (grant.getRoleCategory() == null) {
      return null;
    }
    Map<String, Object> payload = Collections.singletonMap(KEY_ROLE_CATEGORY, grant.getRoleCategory().name());
    return jsonCodec.writeValue(payload);
  }
}
