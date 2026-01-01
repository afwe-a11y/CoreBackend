package com.tenghe.corebackend.kronos.api.dto.product;

import jakarta.validation.constraints.NotNull;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品协议映射更新 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductMappingUpdateDTO {

  /**
   * 协议映射
   */
  @NotNull(message = "协议映射不能为空")
  private Map<String, String> mappings;
}
