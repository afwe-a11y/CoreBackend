package com.tenghe.corebackend.kronos.api.dto.gateway;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建网关请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayCreateDTO {

  /**
   * 网关名称
   */
  @NotBlank(message = "网关名称不能为空")
  @Size(max = 50, message = "网关名称最长50字符")
  private String name;

  /**
   * 网关类型：EDGE/VIRTUAL
   */
  @NotBlank(message = "网关类型不能为空")
  private String type;

  /**
   * 序列号
   */
  @NotBlank(message = "序列号不能为空")
  @Size(max = 20, message = "序列号最长20字符")
  private String sn;

  /**
   * 关联的网关产品ID
   */
  @NotNull(message = "产品ID不能为空")
  private Long productId;

  /**
   * 关联的电站ID
   */
  @NotNull(message = "电站ID不能为空")
  private Long stationId;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;
}
