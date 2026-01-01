package com.tenghe.corebackend.kronos.interfaces.downstream.dto.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建设备模型请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviceModelRequest {

  private String identifier;
  private String name;
  private String source;
  private Long parentModelId;
  private List<DeviceModelPointDto> points;
}
