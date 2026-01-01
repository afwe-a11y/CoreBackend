package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备遥测数据项（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTelemetryItem {

  private String pointIdentifier;
  private Object value;
  private LocalDateTime updatedAt;
}
