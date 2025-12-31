package com.tenghe.corebackend.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RolePermissionPo {
    private Long id;
    private Long appId;
    private Long roleId;
    private String permCode;
    private Instant createTime;
    private Integer deleted;
}
