package com.tenghe.corebackend.api.dto.application;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApplicationDetailResponse {
    private String id;
    private String appName;
    private String appCode;
    private String description;
    private String status;
    private String createdDate;
    private List<Long> permissionIds;
}
