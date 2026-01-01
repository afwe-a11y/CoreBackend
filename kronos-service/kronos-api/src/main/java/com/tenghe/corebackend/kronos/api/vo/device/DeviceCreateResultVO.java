package com.tenghe.corebackend.kronos.api.vo.device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备创建结果 VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCreateResultVO {

  /**
   * 设备ID
   */
  private String id;

  /**
   * 设备密钥
   */
  private String deviceSecret;
}
