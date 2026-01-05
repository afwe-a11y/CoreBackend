package com.tenghe.corebackend.iam.application.service.result;

public class ExternalMemberListItemResult {
  private String username;
  private String sourceOrganizationName;
  private String phone;
  private String email;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSourceOrganizationName() {
    return sourceOrganizationName;
  }

  public void setSourceOrganizationName(String sourceOrganizationName) {
    this.sourceOrganizationName = sourceOrganizationName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
