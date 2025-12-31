package com.tenghe.corebackend.iam.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrganizationApp {
    private Long organizationId;
    private Long appId;
    private boolean deleted;
}
