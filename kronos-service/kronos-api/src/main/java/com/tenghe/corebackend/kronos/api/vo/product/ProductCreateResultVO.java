package com.tenghe.corebackend.kronos.api.vo.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品创建结果 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateResultVO {

  /**
   * 产品ID
   */
  private String id;

  /**
   * 产品Key
   */
  private String productKey;

  /**
   * 产品密钥
   */
  private String productSecret;
}
