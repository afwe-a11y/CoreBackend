package com.tenghe.corebackend.kronos.model;

import com.tenghe.corebackend.kronos.model.enums.DeviceModelSourceEnum;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备模型。
 * 定义设备能力的蓝图，包含标准属性和遥测点定义。
 * 支持单层继承（父模型 -> 子模型）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModel {

  /**
   * 模型ID
   */
  private Long id;

  /**
   * 模型标识符（2-8字符，英文+数字，全局唯一，创建后不可变）
   */
  private String identifier;

  /**
   * 模型名称（最长50字符）
   */
  private String name;

  /**
   * 来源：新建/继承
   */
  private DeviceModelSourceEnum source;

  /**
   * 父模型ID（当source=INHERIT时必填，最大继承深度=1）
   */
  private Long parentModelId;

  /**
   * 描述
   */
  private String description;

  /**
   * 模型点位列表
   */
  private List<ModelPoint> points;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;
}
