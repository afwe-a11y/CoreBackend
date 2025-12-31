package com.tenghe.corebackend.iam.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationInstancePo {
    private Long id;
    private Long orgId;
    private Long templateId;
    private String appCode;
    private String appName;
    private String status;
    private Instant createTime;
    private Integer deleted;
}
