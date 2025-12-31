package com.tenghe.corebackend.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrganizationPo {
    private Long id;
    private String orgName;
    private String orgCode;
    private String description;
    private String status;
    private Instant createTime;
    private Integer deleted;
}
