package com.tenghe.corebackend.kronos.api.vo.device;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备详情响应VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceVO {

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
   * 关联的产品ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long productId;

  /**
   * 产品名称
   */
  private String productName;

  /**
   * 设备Key
   */
  private String deviceKey;

  /**
   * 设备密钥
   */
  private String deviceSecret;

  /**
   * 关联的网关ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long gatewayId;

  /**
   * 网关名称
   */
  private String gatewayName;

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
   * 动态属性
   */
  private Map<String, Object> dynamicAttributes;

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
