package com.tenghe.corebackend.kronos.interfaces.downstream.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新产品请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {

  private String name;
  private String description;
}
