package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入提交响应（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportCommitResponse {

  private int successCount;
  private int failureCount;
  private List<DeviceImportFailure> failures;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DeviceImportFailure {
    private int rowIndex;
    private String message;
  }
}
