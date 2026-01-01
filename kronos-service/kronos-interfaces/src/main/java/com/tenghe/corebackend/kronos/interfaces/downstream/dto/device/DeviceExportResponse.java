package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导出响应（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExportResponse {

  private String fileName;
  private List<DeviceExportRow> rows;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeviceExportRow {
    private String name;
    private String deviceKey;
    private Long productId;
    private Long gatewayId;
    private Long stationId;
  }
}
