package com.tenghe.corebackend.model;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrgMembership {
    private Long organizationId;
    private Long userId;
    private Instant createdAt;
    private boolean deleted;
}
