package com.tenghe.corebackend.kronos.api.vo.device;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备遥测数据 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTelemetryVO {

  /**
   * 点位标识符
   */
  private String pointIdentifier;

  /**
   * 数值
   */
  private Object value;

  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;
}
