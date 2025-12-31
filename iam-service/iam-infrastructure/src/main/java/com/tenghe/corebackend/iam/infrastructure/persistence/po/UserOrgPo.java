package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserOrgPo {
    private Long id;
    private Long userId;
    private Long orgId;
    private String identityType;
    private String status;
    private Instant joinTime;
    private Instant createTime;
    private Integer deleted;
    private Long sourceOrgId;
}
