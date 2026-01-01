package com.tenghe.corebackend.kronos.api.vo.devicemodel;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备模型列表项VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelListVO {

  /**
   * 模型ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 模型标识符
   */
  private String identifier;

  /**
   * 模型名称
   */
  private String name;

  /**
   * 来源：NEW/INHERIT
   */
  private String source;

  /**
   * 父模型名称
   */
  private String parentModelName;

  /**
   * 点位数量
   */
  private Integer pointCount;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;
}
