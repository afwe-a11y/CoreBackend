package com.tenghe.corebackend.controller.web;

import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.api.dto.common.PageResponse;
import com.tenghe.corebackend.api.dto.role.BatchRoleMemberRequest;
import com.tenghe.corebackend.api.dto.role.ConfigureRolePermissionsRequest;
import com.tenghe.corebackend.api.dto.role.CreateRoleRequest;
import com.tenghe.corebackend.api.dto.role.RoleDetailResponse;
import com.tenghe.corebackend.api.dto.role.RoleListItem;
import com.tenghe.corebackend.api.dto.role.RoleMember;
import com.tenghe.corebackend.api.dto.role.UpdateRoleRequest;
import com.tenghe.corebackend.application.command.BatchRoleMemberCommand;
import com.tenghe.corebackend.application.command.ConfigureRolePermissionsCommand;
import com.tenghe.corebackend.application.command.CreateRoleCommand;
import com.tenghe.corebackend.application.command.UpdateRoleCommand;
import com.tenghe.corebackend.application.RoleApplicationService;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.RoleDetailResult;
import com.tenghe.corebackend.application.service.result.RoleListItemResult;
import com.tenghe.corebackend.application.service.result.RoleMemberResult;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault());

    private final RoleApplicationService roleService;

    public RoleController(RoleApplicationService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    public ApiResponse<PageResponse<RoleListItem>> listRoles(
            @RequestParam(value = "appId", required = false) Long appId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        PageResult<RoleListItemResult> result = roleService.listRoles(appId, keyword, page, size);
        List<RoleListItem> items = result.getItems().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
        PageResponse<RoleListItem> response = new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize());
        return ApiResponse.ok(response);
    }

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

    @DeleteMapping("/{roleId}")
    public ApiResponse<Void> deleteRole(@PathVariable("roleId") Long roleId) {
        roleService.deleteRole(roleId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{roleId}/members")
    public ApiResponse<PageResponse<RoleMember>> listRoleMembers(
            @PathVariable("roleId") Long roleId,
            @RequestParam(value = "organizationId", required = false) Long organizationId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        PageResult<RoleMemberResult> result = roleService.listRoleMembers(roleId, organizationId, page, size);
        List<RoleMember> items = result.getItems().stream()
                .map(this::toMember)
                .collect(Collectors.toList());
        PageResponse<RoleMember> response = new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize());
        return ApiResponse.ok(response);
    }

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
