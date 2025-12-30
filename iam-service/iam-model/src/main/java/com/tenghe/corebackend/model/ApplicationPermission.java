package com.tenghe.corebackend.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationPermission {
    private Long id;
    private Long appId;
    private Long permissionId;
    private Instant createdAt;
    private boolean deleted;
}
