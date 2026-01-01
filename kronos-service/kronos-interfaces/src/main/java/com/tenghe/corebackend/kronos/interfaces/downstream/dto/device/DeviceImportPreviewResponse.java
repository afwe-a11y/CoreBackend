package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入预览响应（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportPreviewResponse {

  private int total;
  private int createCount;
  private int updateCount;
  private int invalidCount;
  private List<DeviceImportPreviewItem> items;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeviceImportPreviewItem {
    private int rowIndex;
    private String action;
    private String message;
  }
}
