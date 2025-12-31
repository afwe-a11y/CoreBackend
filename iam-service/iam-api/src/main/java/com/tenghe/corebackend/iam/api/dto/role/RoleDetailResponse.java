package com.tenghe.corebackend.iam.api.dto.role;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoleDetailResponse {
    private String id;
    private String appId;
    private String appName;
    private String roleName;
    private String roleCode;
    private String description;
    private String status;
    private boolean preset;
    private String createdDate;
    private List<Long> permissionIds;
}
