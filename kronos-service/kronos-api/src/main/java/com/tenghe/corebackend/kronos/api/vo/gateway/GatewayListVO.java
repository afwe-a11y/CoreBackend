package com.tenghe.corebackend.kronos.api.vo.gateway;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关列表项VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayListVO {

  /**
   * 网关ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 网关名称
   */
  private String name;

  /**
   * 网关类型
   */
  private String type;

  /**
   * 序列号
   */
  private String sn;

  /**
   * 电站名称
   */
  private String stationName;

  /**
   * 状态
   */
  private String status;

  /**
   * 是否启用
   */
  private Boolean enabled;

  /**
   * 子设备数量
   */
  private Integer deviceCount;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;
}
