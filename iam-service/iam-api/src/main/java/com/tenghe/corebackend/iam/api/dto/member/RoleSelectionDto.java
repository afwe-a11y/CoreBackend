package com.tenghe.corebackend.iam.api.dto.member;

public class RoleSelectionDto {
  private Long appId;
  private String roleCode;

  public Long getAppId() {
    return appId;
  }

  public void setAppId(Long appId) {
    this.appId = appId;
  }

  public String getRoleCode() {
    return roleCode;
  }

  public void setRoleCode(String roleCode) {
    this.roleCode = roleCode;
  }
}
