package com.tenghe.corebackend.iam.infrastructure.persistence.repository;

import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.PermissionMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.PermissionPo;
import com.tenghe.corebackend.iam.interfaces.PermissionRepositoryPort;
import com.tenghe.corebackend.iam.model.Permission;
import com.tenghe.corebackend.iam.model.enums.PermissionStatusEnum;
import com.tenghe.corebackend.iam.model.enums.PermissionTypeEnum;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class MyBatisPermissionRepository implements PermissionRepositoryPort {
  private static final long DEFAULT_TEMPLATE_ID = 0L;

  private final PermissionMapper permissionMapper;

  public MyBatisPermissionRepository(PermissionMapper permissionMapper) {
    this.permissionMapper = permissionMapper;
  }

  @Override
  public Permission save(Permission permission) {
    PermissionPo po = toPo(permission);
    permissionMapper.insert(po);
    return permission;
  }

  @Override
  public Permission update(Permission permission) {
    PermissionPo po = toPo(permission);
    permissionMapper.update(po);
    return permission;
  }

  @Override
  public Permission findById(Long id) {
    return toModel(permissionMapper.findById(id));
  }

  @Override
  public Permission findByCode(String permissionCode) {
    return toModel(permissionMapper.findByCode(permissionCode));
  }

  @Override
  public List<Permission> listAll() {
    return permissionMapper.listAll().stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Permission> findByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return Collections.emptyList();
    }
    return permissionMapper.findByIds(ids).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Permission> findByParentId(Long parentId) {
    if (parentId == null) {
      return Collections.emptyList();
    }
    PermissionPo parent = permissionMapper.findById(parentId);
    if (parent == null) {
      return Collections.emptyList();
    }
    return permissionMapper.findByParentCode(parent.getPermCode()).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<Permission> findAllDescendants(Long parentId) {
    if (parentId == null) {
      return Collections.emptyList();
    }
    List<Permission> all = listAll();
    Map<Long, List<Permission>> childrenByParent = new HashMap<>();
    for (Permission permission : all) {
      Long pid = permission.getParentId();
      if (pid != null) {
        childrenByParent.computeIfAbsent(pid, key -> new ArrayList<>()).add(permission);
      }
    }
    List<Permission> descendants = new ArrayList<>();
    ArrayDeque<Long> queue = new ArrayDeque<>();
    queue.add(parentId);
    while (!queue.isEmpty()) {
      Long current = queue.poll();
      List<Permission> children = childrenByParent.get(current);
      if (children == null) {
        continue;
      }
      for (Permission child : children) {
        descendants.add(child);
        queue.add(child.getId());
      }
    }
    return descendants;
  }

  @Override
  public void updateStatusByIds(Set<Long> ids, PermissionStatusEnum status) {
    if (ids == null || ids.isEmpty()) {
      return;
    }
    permissionMapper.updateStatusByIds(ids, status == null ? null : status.name());
  }

  private Permission toModel(PermissionPo po) {
    if (po == null) {
      return null;
    }
    Permission permission = new Permission();
    permission.setId(po.getId());
    permission.setPermissionCode(po.getPermCode());
    permission.setPermissionName(po.getPermName());
    permission.setPermissionType(PermissionTypeEnum.fromValue(po.getPermType()));
    permission.setStatus(PermissionStatusEnum.fromValue(po.getStatus()));
    permission.setParentId(po.getParentId());
    permission.setSortOrder(po.getSortNo());
    permission.setCreatedAt(po.getCreateTime());
    permission.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    return permission;
  }

  private PermissionPo toPo(Permission permission) {
    PermissionPo po = new PermissionPo();
    po.setId(permission.getId());
    po.setTemplateId(DEFAULT_TEMPLATE_ID);
    po.setPermCode(permission.getPermissionCode());
    po.setPermName(permission.getPermissionName());
    po.setPermType(permission.getPermissionType() == null ? null : permission.getPermissionType().name());
    po.setStatus(permission.getStatus() == null ? null : permission.getStatus().name());
    po.setSortNo(permission.getSortOrder());
    po.setCreateTime(permission.getCreatedAt());
    po.setDeleted(permission.isDeleted() ? 1 : 0);
    if (permission.getParentId() != null) {
      PermissionPo parent = permissionMapper.findById(permission.getParentId());
      if (parent != null) {
        po.setParentCode(parent.getPermCode());
      }
    }
    return po;
  }
}
