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
 * <p>
 * 供上层微服务调用 iam-service 的权限管理功能。
 * 权限是菜单/按钮权限，具有层级结构（树形），支持启用/禁用状态。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>权限树只读，仅提供启用/禁用状态切换</li>
 *   <li>禁用父级菜单时，必须级联禁用所有子菜单/按钮（事务性操作）</li>
 * </ul>
 */
@FeignClient(name = "iam-service", path = "/api/permissions", contextId = "iamPermissionClient")
public interface IamPermissionClient {

    /**
     * 获取权限树
     * <p>
     * 返回完整的权限树结构，包含所有层级的权限节点。
     * </p>
     * 
     * @return 权限树节点列表（顶级节点，每个节点包含子节点）
     */
    @GetMapping("/tree")
    ApiResponse<List<PermissionTreeNode>> getPermissionTree();

    /**
     * 切换权限状态（启用/禁用）
     * <p>
     * 禁用父级权限时，自动级联禁用所有子权限。
     * </p>
     * 
     * @param permissionId 权限ID
     * @param request 状态请求，包含目标状态
     * @return 空响应
     */
    @PutMapping("/{permissionId}/status")
    ApiResponse<Void> togglePermissionStatus(@PathVariable("permissionId") Long permissionId, @RequestBody TogglePermissionStatusRequest request);
}
