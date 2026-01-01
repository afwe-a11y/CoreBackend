package com.tenghe.corebackend.kronos.interfaces.downstream.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品列表项（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListItem {

  private Long id;
  private String productType;
  private String name;
  private String productKey;
  private Long deviceModelId;
  private String accessMode;
  private String description;
}
