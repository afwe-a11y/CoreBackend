package com.tenghe.corebackend.kronos.api.dto.devicemodel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建设备模型请求DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelCreateDTO {

  /**
   * 模型标识符（2-8字符，英文+数字，全局唯一）
   */
  @NotBlank(message = "模型标识符不能为空")
  @Size(min = 2, max = 8, message = "模型标识符长度必须在2-8字符之间")
  @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "模型标识符只能包含英文和数字")
  private String identifier;

  /**
   * 模型名称
   */
  @NotBlank(message = "模型名称不能为空")
  @Size(max = 50, message = "模型名称最长50字符")
  private String name;

  /**
   * 来源：NEW/INHERIT
   */
  @NotBlank(message = "来源不能为空")
  private String source;

  /**
   * 父模型ID（当source=INHERIT时必填）
   */
  private Long parentModelId;

  /**
   * 点位列表
   */
  private List<ModelPointImportDTO> points;

  /**
   * 描述
   */
  @Size(max = 500, message = "描述最长500字符")
  private String description;
}
