package com.tenghe.corebackend.model;

import com.tenghe.corebackend.model.enums.PermissionStatusEnum;
import com.tenghe.corebackend.model.enums.PermissionTypeEnum;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

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
