package com.tenghe.corebackend.api.feign;

import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.api.dto.common.PageResponse;
import com.tenghe.corebackend.api.dto.role.BatchRoleMemberRequest;
import com.tenghe.corebackend.api.dto.role.ConfigureRolePermissionsRequest;
import com.tenghe.corebackend.api.dto.role.CreateRoleRequest;
import com.tenghe.corebackend.api.dto.role.RoleDetailResponse;
import com.tenghe.corebackend.api.dto.role.RoleListItem;
import com.tenghe.corebackend.api.dto.role.RoleMember;
import com.tenghe.corebackend.api.dto.role.UpdateRoleRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * IAM 角色服务 Feign Client
 * <p>
 * 供上层微服务调用 iam-service 的角色管理功能。
 * 角色属于应用，聚合一组权限。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>角色按所属应用分组</li>
 *   <li>角色名称：必填，同一应用内唯一</li>
 *   <li>角色编码：必填，全局唯一</li>
 *   <li>预置角色（如"超级管理员"、"组织管理员"）：名称不可编辑，状态不可切换</li>
 *   <li>删除角色：如果角色已分配给用户，必须先移除成员才能删除</li>
 *   <li>删除：仅支持软删除</li>
 * </ul>
 */
@FeignClient(name = "iam-service", path = "/api/roles", contextId = "iamRoleClient")
public interface IamRoleClient {

    /**
     * 分页查询角色列表
     * <p>
     * 按应用分组查询角色，支持关键词筛选。
     * </p>
     * 
     * @param appId 应用ID（必填）
     * @param keyword 搜索关键词（角色名称/编码模糊匹配）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping
    ApiResponse<PageResponse<RoleListItem>> listRoles(
            @RequestParam("appId") Long appId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    /**
     * 创建角色
     * 
     * @param request 创建请求，包含应用ID、角色名称、角色编码、描述
     * @return 新创建的角色ID
     */
    @PostMapping
    ApiResponse<String> createRole(@RequestBody CreateRoleRequest request);

    /**
     * 获取角色详情
     * 
     * @param roleId 角色ID
     * @return 角色详情，包含基本信息、所属应用、已配置的权限ID列表
     */
    @GetMapping("/{roleId}")
    ApiResponse<RoleDetailResponse> getRoleDetail(@PathVariable("roleId") Long roleId);

    /**
     * 更新角色信息
     * <p>
     * 预置角色的名称不可修改。
     * </p>
     * 
     * @param roleId 角色ID
     * @param request 更新请求，包含角色名称、描述
     * @return 空响应
     */
    @PutMapping("/{roleId}")
    ApiResponse<Void> updateRole(@PathVariable("roleId") Long roleId, @RequestBody UpdateRoleRequest request);

    /**
     * 配置角色权限
     * <p>
     * 权限树数据源必须是角色所属应用的"包含权限子集"，而非全局权限集。
     * </p>
     * 
     * @param roleId 角色ID
     * @param request 配置请求，包含权限ID列表
     * @return 空响应
     */
    @PutMapping("/{roleId}/permissions")
    ApiResponse<Void> configureRolePermissions(@PathVariable("roleId") Long roleId, @RequestBody ConfigureRolePermissionsRequest request);

    /**
     * 删除角色（软删除）
     * <p>
     * 如果角色已分配给用户，必须先移除所有成员才能删除。预置角色不可删除。
     * </p>
     * 
     * @param roleId 角色ID
     * @return 空响应
     */
    @DeleteMapping("/{roleId}")
    ApiResponse<Void> deleteRole(@PathVariable("roleId") Long roleId);

    /**
     * 分页查询角色成员列表
     * <p>
     * 查询拥有该角色的用户列表，按组织筛选。
     * </p>
     * 
     * @param roleId 角色ID
     * @param organizationId 组织ID（必填）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/{roleId}/members")
    ApiResponse<PageResponse<RoleMember>> listRoleMembers(
            @PathVariable("roleId") Long roleId,
            @RequestParam("organizationId") Long organizationId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    /**
     * 批量添加角色成员
     * <p>
     * 为指定角色批量添加用户。候选用户范围受操作者权限约束。
     * </p>
     * 
     * @param roleId 角色ID
     * @param request 批量请求，包含组织ID、用户ID列表
     * @return 空响应
     */
    @PostMapping("/{roleId}/members/batch-add")
    ApiResponse<Void> batchAddMembers(@PathVariable("roleId") Long roleId, @RequestBody BatchRoleMemberRequest request);

    /**
     * 批量移除角色成员
     * <p>
     * 从指定角色批量移除用户。
     * </p>
     * 
     * @param roleId 角色ID
     * @param request 批量请求，包含组织ID、用户ID列表
     * @return 空响应
     */
    @PostMapping("/{roleId}/members/batch-remove")
    ApiResponse<Void> batchRemoveMembers(@PathVariable("roleId") Long roleId, @RequestBody BatchRoleMemberRequest request);
}
