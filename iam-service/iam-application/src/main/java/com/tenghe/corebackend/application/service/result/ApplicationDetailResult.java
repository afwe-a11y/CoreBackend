package com.tenghe.corebackend.application.service.result;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationDetailResult {
    private Long id;
    private String appName;
    private String appCode;
    private String description;
    private String status;
    private Instant createdAt;
    private List<Long> permissionIds;
}
