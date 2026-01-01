package com.tenghe.corebackend.kronos.model;

import com.tenghe.corebackend.kronos.model.enums.DeviceStatusEnum;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备。
 * 实际生成数据的资产实例。
 * 必须定义产品（设备类型）和网关（连接位置）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

  /**
   * 设备ID
   */
  private Long id;

  /**
   * 设备名称（最长50字符）
   */
  private String name;

  /**
   * 关联的产品ID（不可变）
   */
  private Long productId;

  /**
   * 设备Key（最长30字符，产品内或全局唯一，不可变）
   */
  private String deviceKey;

  /**
   * 设备密钥（系统生成）
   */
  private String deviceSecret;

  /**
   * 关联的网关ID（可变）
   */
  private Long gatewayId;

  /**
   * 关联的电站ID（只读，与网关同步）
   */
  private Long stationId;

  /**
   * 状态：在线/离线
   */
  private DeviceStatusEnum status;

  /**
   * 动态属性（存储模型定义的属性值）
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
