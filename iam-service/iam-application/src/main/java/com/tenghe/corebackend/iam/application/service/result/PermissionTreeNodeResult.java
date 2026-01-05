package com.tenghe.corebackend.iam.application.service.result;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PermissionTreeNodeResult {
  private Long id;
  private String permissionCode;
  private String permissionName;
  private String permissionType;
  private String status;
  private Integer sortOrder;
  private List<PermissionTreeNodeResult> children;
}
