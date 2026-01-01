package com.tenghe.corebackend.kronos.interfaces.downstream.dto.product;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建产品请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {

  private String productType;
  private String name;
  private Long deviceModelId;
  private String accessMode;
  private String description;
  private Map<String, String> protocolMapping;
}
