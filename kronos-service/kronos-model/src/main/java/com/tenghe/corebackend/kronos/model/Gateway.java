package com.tenghe.corebackend.kronos.model;

import com.tenghe.corebackend.kronos.model.enums.GatewayStatusEnum;
import com.tenghe.corebackend.kronos.model.enums.GatewayTypeEnum;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关。
 * 代表物理网关设备（边缘网关）或虚拟网关。
 * 必须关联到电站和网关产品。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Gateway {

  /**
   * 网关ID
   */
  private Long id;

  /**
   * 网关名称（最长50字符）
   */
  private String name;

  /**
   * 网关类型（创建后不可变）
   */
  private GatewayTypeEnum type;

  /**
   * 序列号（最长20字符，唯一）
   */
  private String sn;

  /**
   * 关联的网关产品ID
   */
  private Long productId;

  /**
   * 关联的电站ID
   */
  private Long stationId;

  /**
   * 状态：在线/离线
   */
  private GatewayStatusEnum status;

  /**
   * 是否启用（禁用时停止处理该网关下所有子设备的数据）
   */
  private Boolean enabled;

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
