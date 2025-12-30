package com.tenghe.corebackend.application.service;

import com.tenghe.corebackend.application.exception.BusinessException;
import com.tenghe.corebackend.application.service.result.PermissionTreeNodeResult;
import com.tenghe.corebackend.interfaces.PermissionRepositoryPort;
import com.tenghe.corebackend.interfaces.TransactionManagerPort;
import com.tenghe.corebackend.model.Permission;
import com.tenghe.corebackend.model.PermissionStatus;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PermissionApplicationService {
    private final PermissionRepositoryPort permissionRepository;
    private final TransactionManagerPort transactionManager;

    public PermissionApplicationService(
            PermissionRepositoryPort permissionRepository,
            TransactionManagerPort transactionManager) {
        this.permissionRepository = permissionRepository;
        this.transactionManager = transactionManager;
    }

    public List<PermissionTreeNodeResult> getPermissionTree() {
        List<Permission> allPermissions = permissionRepository.listAll();
        return buildTree(allPermissions, null);
    }

    public void togglePermissionStatus(Long permissionId, String status) {
        Permission permission = permissionRepository.findById(permissionId);
        if (permission == null) {
            throw new BusinessException("权限不存在");
        }
        PermissionStatus newStatus = PermissionStatus.fromValue(status);
        if (newStatus == null) {
            throw new BusinessException("无效的状态值");
        }

        transactionManager.doInTransaction(() -> {
            permission.setStatus(newStatus);
            permissionRepository.update(permission);

            if (newStatus == PermissionStatus.DISABLED) {
                List<Permission> descendants = permissionRepository.findAllDescendants(permissionId);
                if (!descendants.isEmpty()) {
                    Set<Long> descendantIds = descendants.stream()
                            .map(Permission::getId)
                            .collect(Collectors.toSet());
                    permissionRepository.updateStatusByIds(descendantIds, PermissionStatus.DISABLED);
                }
            }
        });
    }

    public List<Permission> findByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return permissionRepository.findByIds(ids);
    }

    private List<PermissionTreeNodeResult> buildTree(List<Permission> permissions, Long parentId) {
        List<PermissionTreeNodeResult> nodes = new ArrayList<>();
        for (Permission permission : permissions) {
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

    private PermissionTreeNodeResult toTreeNode(Permission permission) {
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
