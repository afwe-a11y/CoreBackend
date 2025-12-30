package com.tenghe.corebackend.model;

import com.tenghe.corebackend.model.enums.OrganizationStatusEnum;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Organization {
    private Long id;
    private String name;
    private String code;
    private String description;
    private OrganizationStatusEnum status;
    private Instant createdAt;
    private String primaryAdminDisplay;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private boolean deleted;
}
