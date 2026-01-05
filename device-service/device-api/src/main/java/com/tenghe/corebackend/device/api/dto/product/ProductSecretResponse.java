package com.tenghe.corebackend.device.api.dto.product;

public class ProductSecretResponse {
  private String productKey;
  private String productSecret;

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
