package com.tenghe.corebackend.iam.api.dto.application;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateApplicationRequest {
    private String appName;
    private String description;
    private String status;
    private List<Long> permissionIds;
}
