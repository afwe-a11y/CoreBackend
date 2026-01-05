package com.tenghe.corebackend.device.api.dto.product;

public class CreateProductResponse {
  private Long id;
  private String productKey;
  private String productSecret;

  public CreateProductResponse() {
  }

  public CreateProductResponse(Long id, String productKey, String productSecret) {
    this.id = id;
    this.productKey = productKey;
    this.productSecret = productSecret;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getProductKey() {
    return productKey;
  }

  public void setProductKey(String productKey) {
    this.productKey = productKey;
  }

  public String getProductSecret() {
    return productSecret;
  }

  public void setProductSecret(String productSecret) {
    this.productSecret = productSecret;
  }
}
