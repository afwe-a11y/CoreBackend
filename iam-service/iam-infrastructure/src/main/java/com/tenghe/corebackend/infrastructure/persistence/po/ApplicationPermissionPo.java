package com.tenghe.corebackend.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationPermissionPo {
    private Long id;
    private Long appId;
    private String permCode;
    private String status;
    private Instant createTime;
    private Integer deleted;
}
