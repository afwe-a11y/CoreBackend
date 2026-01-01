package com.tenghe.corebackend.kronos.api.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新产品请求DTO。
 * 仅允许修改名称和描述。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

  /**
   * 产品名称
   */
  @NotBlank(message = "产品名称不能为空")
  @Size(max = 50, message = "产品名称最长50字符")
  private String name;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;
}
