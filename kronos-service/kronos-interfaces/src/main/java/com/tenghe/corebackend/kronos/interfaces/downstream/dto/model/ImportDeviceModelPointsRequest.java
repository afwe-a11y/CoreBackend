package com.tenghe.corebackend.kronos.interfaces.downstream.dto.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 导入设备模型点位请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportDeviceModelPointsRequest {

  private List<DeviceModelPointDto> points;
}
