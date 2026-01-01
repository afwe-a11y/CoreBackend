package com.tenghe.corebackend.kronos.api.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品详情响应VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {

  /**
   * 产品ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long id;

  /**
   * 产品名称
   */
  private String name;

  /**
   * 产品Key
   */
  private String productKey;

  /**
   * 产品密钥
   */
  private String productSecret;

  /**
   * 关联的设备模型ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long deviceModelId;

  /**
   * 设备模型名称
   */
  private String deviceModelName;

  /**
   * 接入方式
   */
  private String accessMode;

  /**
   * 协议映射
   */
  private Map<String, String> protocolMappings;

  /**
   * 描述
   */
  private String description;

  /**
   * 设备数量
   */
  private Integer deviceCount;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;

  /**
   * 更新时间
   */
  private LocalDateTime updatedAt;
}
