package com.tenghe.corebackend.kronos.api.vo.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导出结果 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceExportResultVO {

  /**
   * 文件名
   */
  private String fileName;

  /**
   * 导出行数据
   */
  private List<DeviceExportRowVO> rows;
}
