package com.tenghe.corebackend.kronos.model;

import com.tenghe.corebackend.kronos.model.enums.PointDataTypeEnum;
import com.tenghe.corebackend.kronos.model.enums.PointTypeEnum;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 模型点位定义。
 * 描述设备模型中的属性或遥测点。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelPoint {

  /**
   * 点位ID
   */
  private Long id;

  /**
   * 所属设备模型ID
   */
  private Long deviceModelId;

  /**
   * 点位标识符（模型层次内唯一）
   */
  private String identifier;

  /**
   * 点位名称
   */
  private String name;

  /**
   * 点位类型：属性/遥测
   */
  private PointTypeEnum type;

  /**
   * 数据类型
   */
  private PointDataTypeEnum dataType;

  /**
   * 枚举项（当dataType为ENUM时必填）
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
