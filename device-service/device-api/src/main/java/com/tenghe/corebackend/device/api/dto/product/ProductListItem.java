package com.tenghe.corebackend.device.api.dto.product;

public class ProductListItem {
  private Long id;
  private String productType;
  private String name;
  private String productKey;
  private Long deviceModelId;
  private String accessMode;
  private String description;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProductType() {
    return productType;
  }

  public void setProductType(String productType) {
    this.productType = productType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProductKey() {
    return productKey;
  }

  public void setProductKey(String productKey) {
    this.productKey = productKey;
  }

  public Long getDeviceModelId() {
    return deviceModelId;
  }

  public void setDeviceModelId(Long deviceModelId) {
    this.deviceModelId = deviceModelId;
  }

  public String getAccessMode() {
    return accessMode;
  }

  public void setAccessMode(String accessMode) {
    this.accessMode = accessMode;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
