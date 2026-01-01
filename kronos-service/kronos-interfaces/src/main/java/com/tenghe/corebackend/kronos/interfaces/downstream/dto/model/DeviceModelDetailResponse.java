package com.tenghe.corebackend.kronos.interfaces.downstream.dto.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备模型详情响应（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelDetailResponse {

  private Long id;
  private String identifier;
  private String name;
  private String source;
  private Long parentModelId;
  private List<DeviceModelPointDto> points;
}
