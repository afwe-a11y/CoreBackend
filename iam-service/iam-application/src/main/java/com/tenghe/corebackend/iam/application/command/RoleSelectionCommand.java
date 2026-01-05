package com.tenghe.corebackend.iam.application.command;

public class RoleSelectionCommand {
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
