package com.tenghe.corebackend.iam.api.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ToggleUserStatusRequest {
  private String status;
}
