package com.tenghe.corebackend.iam.model;

import com.tenghe.corebackend.iam.model.enums.PermissionStatusEnum;
import com.tenghe.corebackend.iam.model.enums.PermissionTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class Permission {
  private Long id;
  private String permissionCode;
  private String permissionName;
  private Long parentId;
  private PermissionTypeEnum permissionType;
  private Integer sortOrder;
  private PermissionStatusEnum status;
  private Instant createdAt;
  private boolean deleted;
}
