package com.tenghe.corebackend.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleGrant {
    private Long id;
    private Long organizationId;
    private Long userId;
    private Long appId;
    private String roleCode;
    private RoleCategory roleCategory;
    private Instant createdAt;
    private boolean deleted;
}
