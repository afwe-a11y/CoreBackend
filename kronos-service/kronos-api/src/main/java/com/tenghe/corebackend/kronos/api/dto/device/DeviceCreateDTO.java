package com.tenghe.corebackend.kronos.api.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建设备请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCreateDTO {

  /**
   * 设备名称
   */
  @NotBlank(message = "设备名称不能为空")
  @Size(max = 50, message = "设备名称最长50字符")
  private String name;

  /**
   * 关联的产品ID
   */
  @NotNull(message = "产品ID不能为空")
  private Long productId;

  /**
   * 设备Key（可选，不填则系统生成）
   */
  @Size(max = 30, message = "设备Key最长30字符")
  private String deviceKey;

  /**
   * 关联的网关ID
   */
  @NotNull(message = "网关ID不能为空")
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
