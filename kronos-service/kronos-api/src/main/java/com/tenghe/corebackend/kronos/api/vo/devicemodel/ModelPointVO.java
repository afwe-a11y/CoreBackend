package com.tenghe.corebackend.kronos.api.vo.devicemodel;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型点位响应VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPointVO {

  /**
   * 点位ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 所属设备模型ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long deviceModelId;

  /**
   * 点位标识符
   */
  private String identifier;

  /**
   * 点位名称
   */
  private String name;

  /**
   * 点位类型：ATTRIBUTE/TELEMETRY
   */
  private String type;

  /**
   * 数据类型
   */
  private String dataType;

  /**
   * 枚举项
   */
  private List<String> enumItems;

  /**
   * 单位
   */
  private String unit;

  /**
   * 描述
   */
  private String description;
}
