package com.tenghe.corebackend.iam.application.service.result;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleListItemResult {
    private Long id;
    private Long appId;
    private String appName;
    private String roleName;
    private String roleCode;
    private String description;
    private String status;
    private boolean preset;
    private int memberCount;
    private Instant createdAt;
}
