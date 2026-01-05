package com.tenghe.corebackend.device.application.service.result;

public class DeviceModelListItemResult {
  private Long id;
  private String identifier;
  private String name;
  private String source;
  private Long parentModelId;
  private int pointCount;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Long getParentModelId() {
    return parentModelId;
  }

  public void setParentModelId(Long parentModelId) {
    this.parentModelId = parentModelId;
  }

  public int getPointCount() {
    return pointCount;
  }

  public void setPointCount(int pointCount) {
    this.pointCount = pointCount;
  }
}
