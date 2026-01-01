package com.tenghe.corebackend.kronos.api.dto.device;

import jakarta.validation.constraints.Size;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入行DTO。
 * 用于Excel导入时的单行数据。
 * 导入逻辑：ID为空则创建，ID存在则更新。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportDTO {

  /**
   * 设备ID（为空则创建，存在则更新）
   */
  private Long id;

  /**
   * 设备名称
   */
  @Size(max = 50, message = "设备名称最长50字符")
  private String name;

  /**
   * 设备Key
   */
  @Size(max = 30, message = "设备Key最长30字符")
  private String deviceKey;

  /**
   * 网关序列号（用于关联网关）
   */
  private String gatewaySn;

  /**
   * 动态属性
   */
  private Map<String, Object> dynamicAttributes;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;

  /**
   * 行号（用于错误定位）
   */
  private Integer rowNumber;
}
