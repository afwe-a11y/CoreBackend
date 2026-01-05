package com.tenghe.corebackend.iam.api.v1;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.permission.PermissionTreeNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "权限")
public interface PermissionApi {

  @Operation(summary = "查询权限树")
  @GetMapping("/tree")
  ApiResponse<List<PermissionTreeNode>> getPermissionTree();

  @Operation(summary = "查询角色权限")
  @GetMapping("/role/{roleId}")
  ApiResponse<List<Long>> getRolePermissions(@PathVariable("roleId") Long roleId);
}
