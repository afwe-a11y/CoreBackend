package com.tenghe.corebackend.kronos.api.vo.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入预览结果 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportPreviewResultVO {

  /**
   * 总数
   */
  private int total;

  /**
   * 创建数量
   */
  private int createCount;

  /**
   * 更新数量
   */
  private int updateCount;

  /**
   * 无效数量
   */
  private int invalidCount;

  /**
   * 预览项
   */
  private List<PreviewItem> items;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PreviewItem {
    private int rowIndex;
    private String action;
    private String message;
  }
}
