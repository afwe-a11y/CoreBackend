package com.tenghe.corebackend.api.feign;

import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.api.dto.permission.PermissionTreeNode;
import com.tenghe.corebackend.api.dto.permission.TogglePermissionStatusRequest;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * IAM 权限服务 Feign Client
 * 供上层微服务调用权限管理功能
 */
@FeignClient(name = "iam-service", path = "/api/permissions", contextId = "iamPermissionClient")
public interface IamPermissionClient {

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    ApiResponse<List<PermissionTreeNode>> getPermissionTree();

    /**
     * 切换权限状态
     */
    @PutMapping("/{permissionId}/status")
    ApiResponse<Void> togglePermissionStatus(@PathVariable("permissionId") Long permissionId, @RequestBody TogglePermissionStatusRequest request);
}
