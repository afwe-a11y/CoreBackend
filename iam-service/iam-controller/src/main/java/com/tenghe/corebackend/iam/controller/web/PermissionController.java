package com.tenghe.corebackend.iam.controller.web;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.permission.PermissionTreeNode;
import com.tenghe.corebackend.iam.api.dto.permission.TogglePermissionStatusRequest;
import com.tenghe.corebackend.iam.application.PermissionApplicationService;
import com.tenghe.corebackend.iam.application.service.result.PermissionTreeNodeResult;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限管理 HTTP 入口，面向服务间调用。
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionApplicationService permissionService;

    public PermissionController(PermissionApplicationService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * 获取权限树。
     */
    @GetMapping("/tree")
    public ApiResponse<List<PermissionTreeNode>> getPermissionTree() {
        List<PermissionTreeNodeResult> results = permissionService.getPermissionTree();
        List<PermissionTreeNode> tree = results.stream()
                .map(this::toTreeNode)
                .toList();
        return ApiResponse.ok(tree);
    }

    /**
     * 切换权限状态。
     */
    @PutMapping("/{permissionId}/status")
    public ApiResponse<Void> togglePermissionStatus(
            @PathVariable("permissionId") Long permissionId,
            @RequestBody TogglePermissionStatusRequest request) {
        permissionService.togglePermissionStatus(permissionId, request.getStatus());
        return ApiResponse.ok(null);
    }

    private PermissionTreeNode toTreeNode(PermissionTreeNodeResult result) {
        PermissionTreeNode node = new PermissionTreeNode();
        node.setId(String.valueOf(result.getId()));
        node.setPermissionCode(result.getPermissionCode());
        node.setPermissionName(result.getPermissionName());
        node.setPermissionType(result.getPermissionType());
        node.setStatus(formatStatus(result.getStatus()));
        node.setSortOrder(result.getSortOrder());
        if (result.getChildren() != null) {
            node.setChildren(result.getChildren().stream()
                    .map(this::toTreeNode)
                    .toList());
        }
        return node;
    }

    private String formatStatus(String status) {
        if (status == null) {
            return null;
        }
        if ("ENABLED".equals(status)) {
            return "启用";
        }
        if ("DISABLED".equals(status)) {
            return "停用";
        }
        return status;
    }
}
