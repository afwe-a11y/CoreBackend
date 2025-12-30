package com.tenghe.corebackend.api.dto.permission;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TogglePermissionStatusRequest {
    private String status;
}
