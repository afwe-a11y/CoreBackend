package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRolePo {
    private Long id;
    private Long userId;
    private Long orgId;
    private Long appId;
    private Long roleId;
    private String assetScope;
    private Instant createTime;
    private Integer deleted;
    private String roleCode;
}
