package com.tenghe.corebackend.kronos.api.vo.devicemodel;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备模型详情 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelDetailVO {

  /**
   * 模型ID
   */
  private String id;

  /**
   * 标识符
   */
  private String identifier;

  /**
   * 名称
   */
  private String name;

  /**
   * 来源
   */
  private String source;

  /**
   * 父模型ID
   */
  private String parentModelId;

  /**
   * 点位列表
   */
  private List<ModelPointVO> points;
}
