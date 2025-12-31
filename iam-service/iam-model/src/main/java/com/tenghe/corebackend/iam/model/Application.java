package com.tenghe.corebackend.iam.model;

import com.tenghe.corebackend.iam.model.enums.ApplicationStatusEnum;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Application {
    private Long id;
    private String appName;
    private String appCode;
    private String description;
    private ApplicationStatusEnum status;
    private Instant createdAt;
    private boolean deleted;
}
