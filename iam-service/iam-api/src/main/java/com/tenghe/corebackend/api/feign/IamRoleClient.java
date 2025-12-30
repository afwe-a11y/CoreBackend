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
 * 供上层微服务调用角色管理功能
 */
@FeignClient(name = "iam-service", path = "/api/roles", contextId = "iamRoleClient")
public interface IamRoleClient {

    /**
     * 分页查询角色列表
     */
    @GetMapping
    ApiResponse<PageResponse<RoleListItem>> listRoles(
            @RequestParam("appId") Long appId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    /**
     * 创建角色
     */
    @PostMapping
    ApiResponse<String> createRole(@RequestBody CreateRoleRequest request);

    /**
     * 获取角色详情
     */
    @GetMapping("/{roleId}")
    ApiResponse<RoleDetailResponse> getRoleDetail(@PathVariable("roleId") Long roleId);

    /**
     * 更新角色
     */
    @PutMapping("/{roleId}")
    ApiResponse<Void> updateRole(@PathVariable("roleId") Long roleId, @RequestBody UpdateRoleRequest request);

    /**
     * 配置角色权限
     */
    @PutMapping("/{roleId}/permissions")
    ApiResponse<Void> configureRolePermissions(@PathVariable("roleId") Long roleId, @RequestBody ConfigureRolePermissionsRequest request);

    /**
     * 删除角色
     */
    @DeleteMapping("/{roleId}")
    ApiResponse<Void> deleteRole(@PathVariable("roleId") Long roleId);

    /**
     * 获取角色成员列表
     */
    @GetMapping("/{roleId}/members")
    ApiResponse<PageResponse<RoleMember>> listRoleMembers(
            @PathVariable("roleId") Long roleId,
            @RequestParam("organizationId") Long organizationId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    /**
     * 批量添加角色成员
     */
    @PostMapping("/{roleId}/members/batch-add")
    ApiResponse<Void> batchAddMembers(@PathVariable("roleId") Long roleId, @RequestBody BatchRoleMemberRequest request);

    /**
     * 批量移除角色成员
     */
    @PostMapping("/{roleId}/members/batch-remove")
    ApiResponse<Void> batchRemoveMembers(@PathVariable("roleId") Long roleId, @RequestBody BatchRoleMemberRequest request);
}
