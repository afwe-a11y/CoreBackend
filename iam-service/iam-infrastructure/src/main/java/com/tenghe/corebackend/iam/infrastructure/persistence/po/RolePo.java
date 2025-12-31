package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RolePo {
    private Long id;
    private Long appId;
    private String roleCode;
    private String roleName;
    private String roleType;
    private String description;
    private Instant createTime;
    private Integer deleted;
}
