package com.tenghe.corebackend.iam.application.service.impl;

import com.tenghe.corebackend.iam.application.PermissionApplicationService;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import com.tenghe.corebackend.iam.application.service.result.PermissionTreeNodeResult;
import com.tenghe.corebackend.iam.interfaces.ports.PermissionRepository;
import com.tenghe.corebackend.iam.interfaces.ports.TransactionManager;
import com.tenghe.corebackend.iam.interfaces.portdata.PermissionPortData;
import com.tenghe.corebackend.iam.model.enums.PermissionStatusEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PermissionApplicationServiceImpl implements PermissionApplicationService {
  private final PermissionRepository permissionRepository;
  private final TransactionManager transactionManager;

  public PermissionApplicationServiceImpl(
      PermissionRepository permissionRepository,
      TransactionManager transactionManager) {
    this.permissionRepository = permissionRepository;
    this.transactionManager = transactionManager;
  }

  @Override
  public List<PermissionTreeNodeResult> getPermissionTree() {
    List<PermissionPortData> allPermissions = permissionRepository.listAll();
    return buildTree(allPermissions, null);
  }

  @Override
  public void togglePermissionStatus(Long permissionId, String status) {
    PermissionPortData permission = permissionRepository.findById(permissionId);
    if (permission == null) {
      throw new BusinessException("权限不存在");
    }
    PermissionStatusEnum newStatus = PermissionStatusEnum.fromValue(status);
    if (newStatus == null) {
      throw new BusinessException("无效的状态值");
    }

    transactionManager.doInTransaction(() -> {
      permission.setStatus(newStatus);
      permissionRepository.update(permission);

      if (newStatus == PermissionStatusEnum.DISABLED) {
        List<PermissionPortData> descendants = permissionRepository.findAllDescendants(permissionId);
        if (!descendants.isEmpty()) {
          Set<Long> descendantIds = descendants.stream()
              .map(PermissionPortData::getId)
              .collect(Collectors.toSet());
          permissionRepository.updateStatusByIds(descendantIds, PermissionStatusEnum.DISABLED);
        }
      }
    });
  }

  public List<PermissionPortData> findByIds(Set<Long> ids) {
    if (ids == null || ids.isEmpty()) {
      return new ArrayList<>();
    }
    return permissionRepository.findByIds(ids);
  }

  private List<PermissionTreeNodeResult> buildTree(List<PermissionPortData> permissions, Long parentId) {
    List<PermissionTreeNodeResult> nodes = new ArrayList<>();
    for (PermissionPortData permission : permissions) {
      boolean isRoot = parentId == null && permission.getParentId() == null;
      boolean isChild = parentId != null && parentId.equals(permission.getParentId());
      if (isRoot || isChild) {
        PermissionTreeNodeResult node = toTreeNode(permission);
        node.setChildren(buildTree(permissions, permission.getId()));
        nodes.add(node);
      }
    }
    nodes.sort(Comparator.comparing(PermissionTreeNodeResult::getSortOrder, Comparator.nullsLast(Comparator.naturalOrder())));
    return nodes;
  }

  private PermissionTreeNodeResult toTreeNode(PermissionPortData permission) {
    PermissionTreeNodeResult node = new PermissionTreeNodeResult();
    node.setId(permission.getId());
    node.setPermissionCode(permission.getPermissionCode());
    node.setPermissionName(permission.getPermissionName());
    node.setPermissionType(permission.getPermissionType() == null ? null : permission.getPermissionType().name());
    node.setStatus(permission.getStatus() == null ? null : permission.getStatus().name());
    node.setSortOrder(permission.getSortOrder());
    return node;
  }
}
