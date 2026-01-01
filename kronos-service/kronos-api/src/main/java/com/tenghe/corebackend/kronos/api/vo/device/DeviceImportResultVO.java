package com.tenghe.corebackend.kronos.api.vo.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入结果VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportResultVO {

  /**
   * 成功数量
   */
  private Integer successCount;

  /**
   * 失败数量
   */
  private Integer failCount;

  /**
   * 创建数量
   */
  private Integer createCount;

  /**
   * 更新数量
   */
  private Integer updateCount;

  /**
   * 错误详情
   */
  private List<ImportError> errors;

  /**
   * 导入错误详情
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ImportError {
    /**
     * 行号
     */
    private Integer rowNumber;

    /**
     * 设备Key
     */
    private String deviceKey;

    /**
     * 错误原因
     */
    private String reason;
  }
}
