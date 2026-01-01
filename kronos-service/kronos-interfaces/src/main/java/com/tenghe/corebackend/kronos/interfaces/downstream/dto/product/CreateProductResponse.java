package com.tenghe.corebackend.kronos.interfaces.downstream.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建产品响应（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductResponse {

  private Long id;
  private String productKey;
  private String productSecret;
}
