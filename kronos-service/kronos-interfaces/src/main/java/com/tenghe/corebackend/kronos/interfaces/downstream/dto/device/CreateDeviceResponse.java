package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建设备响应（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeviceResponse {

  private Long id;
  private String deviceSecret;
}
