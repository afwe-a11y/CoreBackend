package com.tenghe.corebackend.kronos.api.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新设备请求DTO。
 * productId和deviceKey创建后不可变。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceUpdateDTO {

  /**
   * 设备名称
   */
  @NotBlank(message = "设备名称不能为空")
  @Size(max = 50, message = "设备名称最长50字符")
  private String name;

  /**
   * 关联的网关ID（可变）
   */
  private Long gatewayId;

  /**
   * 动态属性
   */
  private Map<String, Object> dynamicAttributes;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;
}
