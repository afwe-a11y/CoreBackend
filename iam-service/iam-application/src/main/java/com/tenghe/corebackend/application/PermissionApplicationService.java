package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.service.result.PermissionTreeNodeResult;
import java.util.List;

/**
 * 权限管理应用服务接口
 * <p>
 * 提供权限树查询和权限状态管理功能。
 * 权限是菜单/按钮权限，具有层级结构（树形），支持启用/禁用状态。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>权限树只读，仅提供启用/禁用状态切换</li>
 *   <li>禁用父级菜单时，必须级联禁用所有子菜单/按钮（事务性操作）</li>
 * </ul>
 */
public interface PermissionApplicationService {

    /**
     * 获取权限树
     * <p>
     * 返回完整的权限树结构，包含所有层级的权限节点。
     * </p>
     * 
     * @return 权限树节点列表（顶级节点，每个节点包含子节点）
     */
    List<PermissionTreeNodeResult> getPermissionTree();

    /**
     * 切换权限状态（启用/禁用）
     * <p>
     * 禁用父级权限时，自动级联禁用所有子权限。
     * </p>
     * 
     * @param permissionId 权限ID
     * @param status 目标状态：ENABLED(启用) / DISABLED(禁用)
     * @throws BusinessException 权限不存在、无效的状态值
     */
    void togglePermissionStatus(Long permissionId, String status);
}
