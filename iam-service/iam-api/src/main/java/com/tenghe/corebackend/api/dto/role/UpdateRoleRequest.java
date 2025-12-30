package com.tenghe.corebackend.api.dto.role;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateRoleRequest {
    private String roleName;
    private String description;
    private String status;
}
