package com.tenghe.corebackend.kronos.model;

import com.tenghe.corebackend.kronos.model.enums.AccessModeEnum;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品。
 * 定义设备通信契约，包含协议、接入方式和安全边界。
 * 创建后ProductKey、ProductSecret、DeviceModelId、AccessMode不可变。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  /**
   * 产品ID
   */
  private Long id;

  /**
   * 产品名称（最长50字符）
   */
  private String name;

  /**
   * 产品Key（全局唯一，系统生成或手动设置，不可变）
   */
  private String productKey;

  /**
   * 产品密钥（最长30字符，英文/数字，不可变）
   */
  private String productSecret;

  /**
   * 关联的设备模型ID（不可变）
   */
  private Long deviceModelId;

  /**
   * 接入方式（不可变）
   */
  private AccessModeEnum accessMode;

  /**
   * 协议映射：模型点位ID <-> 物理采集名
   */
  private Map<String, String> protocolMappings;

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
