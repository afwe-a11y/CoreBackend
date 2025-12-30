package com.tenghe.corebackend.application.service.result;

import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationListItemResult {
    private Long id;
    private String appName;
    private String appCode;
    private String description;
    private String status;
    private Instant createdAt;
}
