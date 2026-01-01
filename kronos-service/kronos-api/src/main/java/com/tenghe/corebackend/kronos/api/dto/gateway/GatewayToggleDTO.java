package com.tenghe.corebackend.kronos.api.dto.gateway;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关启用/禁用请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToggleDTO {

  /**
   * 是否启用
   */
  @NotNull(message = "启用状态不能为空")
  private Boolean enabled;
}
