package com.tenghe.corebackend.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationTemplatePo {
    private Long id;
    private String templateCode;
    private String templateName;
    private String description;
    private String status;
    private Instant createTime;
    private Integer deleted;
}
