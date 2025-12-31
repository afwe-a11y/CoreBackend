package com.tenghe.corebackend.device.infrastructure.persistence.po;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceModelPointPo {
    private Long id;
    private Long modelId;
    private String pointType;
    private String dataType;
    private String identifier;
    private String name;
    private String enumItemsJson;
    private Integer sortNo;
    private String description;
    private Instant createTime;
    private Integer deleted;
}
