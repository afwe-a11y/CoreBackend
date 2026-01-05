package com.tenghe.corebackend.iam.api.dto.organization;

public class AssignAdminRequest {
  private Long userId;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
}
