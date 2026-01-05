package com.tenghe.corebackend.iam.api.dto.permission;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PermissionTreeNode {
  private String id;
  private String permissionCode;
  private String permissionName;
  private String permissionType;
  private String status;
  private Integer sortOrder;
  private List<PermissionTreeNode> children;
}
