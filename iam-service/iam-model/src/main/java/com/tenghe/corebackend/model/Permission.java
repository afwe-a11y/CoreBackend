package com.tenghe.corebackend.model;

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
    private PermissionType permissionType;
    private Integer sortOrder;
    private PermissionStatus status;
    private Instant createdAt;
    private boolean deleted;
}
