package com.tenghe.corebackend.iam.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExternalMembership {
    private Long organizationId;
    private Long userId;
    private Long sourceOrganizationId;
    private Instant createdAt;
    private boolean deleted;
}
