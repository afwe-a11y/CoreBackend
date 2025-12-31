package com.tenghe.corebackend.iam.application;

import java.util.List;
import java.util.Set;

/**
 * 权限查询服务
 * 供其他微服务调用进行权限校验
 * 这是基座服务最核心的对外接口之一
 */
public interface PermissionQueryApplicationService {

    /**
     * 获取用户在指定组织下的所有权限码
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @return 权限码集合
     */
    Set<String> getUserPermissionCodes(Long userId, Long organizationId);

    /**
     * 校验用户是否拥有指定权限
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @param permissionCode 权限码
     * @return 是否拥有权限
     */
    boolean hasPermission(Long userId, Long organizationId, String permissionCode);

    /**
     * 批量校验用户是否拥有指定权限
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @param permissionCodes 权限码列表
     * @return 用户拥有的权限码（交集）
     */
    Set<String> checkPermissions(Long userId, Long organizationId, List<String> permissionCodes);

    /**
     * 获取用户可访问的组织ID列表
     * @param userId 用户ID
     * @return 组织ID列表
     */
    List<Long> getAccessibleOrganizationIds(Long userId);

    /**
     * 获取用户在指定组织下的角色码列表
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @return 角色码列表
     */
    List<String> getUserRoleCodes(Long userId, Long organizationId);

    /**
     * 校验用户是否拥有指定角色
     * @param userId 用户ID
     * @param organizationId 组织ID
     * @param roleCode 角色码
     * @return 是否拥有角色
     */
    boolean hasRole(Long userId, Long organizationId, String roleCode);
}
