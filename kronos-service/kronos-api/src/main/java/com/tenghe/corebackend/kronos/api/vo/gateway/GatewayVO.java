package com.tenghe.corebackend.kronos.api.vo.gateway;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关详情响应VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayVO {

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
   * 网关类型：EDGE/VIRTUAL
   */
  private String type;

  /**
   * 序列号
   */
  private String sn;

  /**
   * 关联的产品ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long productId;

  /**
   * 产品名称
   */
  private String productName;

  /**
   * 关联的电站ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long stationId;

  /**
   * 电站名称
   */
  private String stationName;

  /**
   * 状态：ONLINE/OFFLINE
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
   * 描述
   */
  private String description;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;
}
