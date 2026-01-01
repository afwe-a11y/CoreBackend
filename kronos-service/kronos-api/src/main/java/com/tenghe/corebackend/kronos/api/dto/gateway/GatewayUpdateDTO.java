package com.tenghe.corebackend.kronos.api.dto.gateway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新网关请求DTO。
 * 网关类型创建后不可变。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayUpdateDTO {

  /**
   * 网关名称
   */
  @NotBlank(message = "网关名称不能为空")
  @Size(max = 50, message = "网关名称最长50字符")
  private String name;

  /**
   * 序列号
   */
  @NotBlank(message = "序列号不能为空")
  @Size(max = 20, message = "序列号最长20字符")
  private String sn;

  /**
   * 产品ID
   */
  private Long productId;

  /**
   * 电站ID
   */
  private Long stationId;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;
}
