package com.tenghe.corebackend.kronos.api.dto.devicemodel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新设备模型请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelUpdateDTO {

  /**
   * 模型名称
   */
  @NotBlank(message = "模型名称不能为空")
  @Size(max = 50, message = "模型名称最长50字符")
  private String name;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;
}
