package com.tenghe.corebackend.kronos.api.vo.product;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品列表项VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListVO {

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
   * 设备模型名称
   */
  private String deviceModelName;

  /**
   * 接入方式
   */
  private String accessMode;

  /**
   * 设备数量
   */
  private Integer deviceCount;

  /**
   * 创建时间
   */
  private LocalDateTime createdAt;
}
