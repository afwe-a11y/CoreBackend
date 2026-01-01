package com.tenghe.corebackend.kronos.api.dto.devicemodel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型点位请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPointDTO {

  /**
   * 点位ID（更新时必填）
   */
  private Long id;

  /**
   * 点位标识符
   */
  @NotBlank(message = "点位标识符不能为空")
  @Size(max = 50, message = "点位标识符最长50字符")
  private String identifier;

  /**
   * 点位名称
   */
  @NotBlank(message = "点位名称不能为空")
  @Size(max = 50, message = "点位名称最长50字符")
  private String name;

  /**
   * 点位类型：ATTRIBUTE/TELEMETRY
   */
  @NotBlank(message = "点位类型不能为空")
  private String type;

  /**
   * 数据类型：INT/FLOAT/DOUBLE/STRING/ENUM/BOOL/DATETIME
   */
  @NotBlank(message = "数据类型不能为空")
  private String dataType;

  /**
   * 枚举项（当dataType为ENUM时必填）
   */
  private List<String> enumItems;

  /**
   * 单位
   */
  @Size(max = 20, message = "单位最长20字符")
  private String unit;

  /**
   * 描述
   */
  @Size(max = 200, message = "描述最长200字符")
  private String description;
}
