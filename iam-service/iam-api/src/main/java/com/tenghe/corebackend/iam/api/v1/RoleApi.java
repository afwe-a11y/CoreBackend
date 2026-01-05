package com.tenghe.corebackend.iam.api.v1;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.common.PageResponse;
import com.tenghe.corebackend.iam.api.dto.role.CreateRoleRequest;
import com.tenghe.corebackend.iam.api.dto.role.RoleListItem;
import com.tenghe.corebackend.iam.api.dto.role.UpdateRoleRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "角色")
public interface RoleApi {

  @Operation(summary = "查询角色分页")
  @GetMapping
  ApiResponse<PageResponse<RoleListItem>> listRoles(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "keyword", required = false) String keyword);

  @Operation(summary = "创建角色")
  @PostMapping
  ApiResponse<Long> createRole(@RequestBody CreateRoleRequest request);

  @Operation(summary = "更新角色")
  @PutMapping("/{roleId}")
  ApiResponse<Void> updateRole(
      @PathVariable("roleId") Long roleId,
      @RequestBody UpdateRoleRequest request);

  @Operation(summary = "删除角色")
  @DeleteMapping("/{roleId}")
  ApiResponse<Void> deleteRole(@PathVariable("roleId") Long roleId);
}
