package com.tenghe.corebackend.iam.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RolePermission {
    private Long id;
    private Long roleId;
    private Long permissionId;
    private Instant createdAt;
    private boolean deleted;
}
