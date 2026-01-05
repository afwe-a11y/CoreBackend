package com.tenghe.corebackend.iam.api.dto.organization;

import java.util.List;

public class CreateOrganizationRequest {
  private String name;
  private String code;
  private String description;
  private List<Long> appIds;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<Long> getAppIds() {
    return appIds;
  }

  public void setAppIds(List<Long> appIds) {
    this.appIds = appIds;
  }
}
