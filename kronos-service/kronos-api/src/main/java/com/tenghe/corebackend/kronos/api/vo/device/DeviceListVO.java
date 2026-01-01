package com.tenghe.corebackend.kronos.api.vo.device;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备列表项VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceListVO {

  /**
   * 设备ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 设备名称
   */
  private String name;

  /**
   * 设备Key
   */
  private String deviceKey;

  /**
   * 产品名称
   */
  private String productName;

  /**
   * 网关名称
   */
  private String gatewayName;

  /**
   * 电站名称
   */
  private String stationName;

  /**
   * 状态
   */
  private String status;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;
}
