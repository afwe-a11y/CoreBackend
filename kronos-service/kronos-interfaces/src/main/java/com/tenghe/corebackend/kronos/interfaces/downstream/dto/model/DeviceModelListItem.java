package com.tenghe.corebackend.kronos.interfaces.downstream.dto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备模型列表项（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelListItem {

  private Long id;
  private String identifier;
  private String name;
  private String source;
  private Long parentModelId;
  private Integer pointCount;
}
