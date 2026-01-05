package com.tenghe.corebackend.iam.api.dto.permission;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TogglePermissionStatusRequest {
  private String status;
}
