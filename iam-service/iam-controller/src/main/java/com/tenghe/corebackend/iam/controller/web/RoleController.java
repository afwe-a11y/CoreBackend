package com.tenghe.corebackend.iam.controller.web;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.common.PageResponse;
import com.tenghe.corebackend.iam.api.dto.role.*;
import com.tenghe.corebackend.iam.application.RoleApplicationService;
import com.tenghe.corebackend.iam.application.command.BatchRoleMemberCommand;
import com.tenghe.corebackend.iam.application.command.ConfigureRolePermissionsCommand;
import com.tenghe.corebackend.iam.application.command.CreateRoleCommand;
import com.tenghe.corebackend.iam.application.command.UpdateRoleCommand;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import com.tenghe.corebackend.iam.application.service.result.RoleDetailResult;
import com.tenghe.corebackend.iam.application.service.result.RoleListItemResult;
import com.tenghe.corebackend.iam.application.service.result.RoleMemberResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 角色管理 HTTP 入口，面向服务间调用。
 */
@RestController
@RequestMapping("/api/roles")
public class RoleController {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      .withZone(ZoneId.systemDefault());

  private final RoleApplicationService roleService;

  public RoleController(RoleApplicationService roleService) {
    this.roleService = roleService;
  }

  /**
   * 分页查询角色列表。
   */
  @GetMapping
  public ApiResponse<PageResponse<RoleListItem>> listRoles(
      @RequestParam(value = "appId", required = false) Long appId,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    PageResult<RoleListItemResult> result = roleService.listRoles(appId, keyword, page, size);
    List<RoleListItem> items = result.getItems().stream()
        .map(this::toListItem)
        .toList();
    PageResponse<RoleListItem> response = new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize());
    return ApiResponse.ok(response);
  }

  /**
   * 创建角色。
   */
  @PostMapping
  public ApiResponse<String> createRole(@RequestBody CreateRoleRequest request) {
    CreateRoleCommand command = new CreateRoleCommand();
    command.setAppId(request.getAppId());
    command.setRoleName(request.getRoleName());
    command.setRoleCode(request.getRoleCode());
    command.setDescription(request.getDescription());
    Long id = roleService.createRole(command);
    return ApiResponse.ok(String.valueOf(id));
  }

  /**
   * 获取角色详情。
   */
  @GetMapping("/{roleId}")
  public ApiResponse<RoleDetailResponse> getRoleDetail(@PathVariable("roleId") Long roleId) {
    RoleDetailResult result = roleService.getRoleDetail(roleId);
    RoleDetailResponse response = new RoleDetailResponse();
    response.setId(String.valueOf(result.getId()));
    response.setAppId(String.valueOf(result.getAppId()));
    response.setAppName(result.getAppName());
    response.setRoleName(result.getRoleName());
    response.setRoleCode(result.getRoleCode());
    response.setDescription(result.getDescription());
    response.setStatus(formatStatus(result.getStatus()));
    response.setPreset(result.isPreset());
    response.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
    response.setPermissionIds(result.getPermissionIds());
    return ApiResponse.ok(response);
  }

  /**
   * 更新角色信息。
   */
  @PutMapping("/{roleId}")
  public ApiResponse<Void> updateRole(
      @PathVariable("roleId") Long roleId,
      @RequestBody UpdateRoleRequest request) {
    UpdateRoleCommand command = new UpdateRoleCommand();
    command.setRoleId(roleId);
    command.setRoleName(request.getRoleName());
    command.setDescription(request.getDescription());
    command.setStatus(request.getStatus());
    roleService.updateRole(command);
    return ApiResponse.ok(null);
  }

  /**
   * 配置角色权限。
   */
  @PutMapping("/{roleId}/permissions")
  public ApiResponse<Void> configureRolePermissions(
      @PathVariable("roleId") Long roleId,
      @RequestBody ConfigureRolePermissionsRequest request) {
    ConfigureRolePermissionsCommand command = new ConfigureRolePermissionsCommand();
    command.setRoleId(roleId);
    command.setPermissionIds(request.getPermissionIds());
    roleService.configureRolePermissions(command);
    return ApiResponse.ok(null);
  }

  /**
   * 删除角色。
   */
  @DeleteMapping("/{roleId}")
  public ApiResponse<Void> deleteRole(@PathVariable("roleId") Long roleId) {
    roleService.deleteRole(roleId);
    return ApiResponse.ok(null);
  }

  /**
   * 分页查询角色成员。
   */
  @GetMapping("/{roleId}/members")
  public ApiResponse<PageResponse<RoleMember>> listRoleMembers(
      @PathVariable("roleId") Long roleId,
      @RequestParam(value = "organizationId", required = false) Long organizationId,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    PageResult<RoleMemberResult> result = roleService.listRoleMembers(roleId, organizationId, page, size);
    List<RoleMember> items = result.getItems().stream()
        .map(this::toMember)
        .toList();
    PageResponse<RoleMember> response = new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize());
    return ApiResponse.ok(response);
  }

  /**
   * 批量添加角色成员。
   */
  @PostMapping("/{roleId}/members")
  public ApiResponse<Void> batchAddMembers(
      @PathVariable("roleId") Long roleId,
      @RequestBody BatchRoleMemberRequest request) {
    BatchRoleMemberCommand command = new BatchRoleMemberCommand();
    command.setRoleId(roleId);
    command.setOrganizationId(request.getOrganizationId());
    command.setUserIds(request.getUserIds());
    roleService.batchAddMembers(command);
    return ApiResponse.ok(null);
  }

  /**
   * 批量移除角色成员。
   */
  @DeleteMapping("/{roleId}/members")
  public ApiResponse<Void> batchRemoveMembers(
      @PathVariable("roleId") Long roleId,
      @RequestBody BatchRoleMemberRequest request) {
    BatchRoleMemberCommand command = new BatchRoleMemberCommand();
    command.setRoleId(roleId);
    command.setOrganizationId(request.getOrganizationId());
    command.setUserIds(request.getUserIds());
    roleService.batchRemoveMembers(command);
    return ApiResponse.ok(null);
  }

  private RoleListItem toListItem(RoleListItemResult result) {
    RoleListItem item = new RoleListItem();
    item.setId(String.valueOf(result.getId()));
    item.setAppId(String.valueOf(result.getAppId()));
    item.setAppName(result.getAppName());
    item.setRoleName(result.getRoleName());
    item.setRoleCode(result.getRoleCode());
    item.setDescription(result.getDescription());
    item.setStatus(formatStatus(result.getStatus()));
    item.setPreset(result.isPreset());
    item.setMemberCount(result.getMemberCount());
    item.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
    return item;
  }

  private RoleMember toMember(RoleMemberResult result) {
    RoleMember member = new RoleMember();
    member.setUserId(String.valueOf(result.getUserId()));
    member.setUsername(result.getUsername());
    member.setName(result.getName());
    member.setPhone(result.getPhone());
    member.setEmail(result.getEmail());
    return member;
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
