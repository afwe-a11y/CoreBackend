package com.tenghe.corebackend.kronos.api.vo.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入提交结果 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportCommitResultVO {

  /**
   * 成功数量
   */
  private int successCount;

  /**
   * 失败数量
   */
  private int failureCount;

  /**
   * 失败详情
   */
  private List<FailureItem> failures;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class FailureItem {
    private int rowIndex;
    private String message;
  }
}
