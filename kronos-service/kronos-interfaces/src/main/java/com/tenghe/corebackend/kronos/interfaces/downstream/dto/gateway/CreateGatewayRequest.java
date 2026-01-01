package com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 创建网关请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGatewayRequest {

  private String name;
  private String type;
  private String sn;
  private Long productId;
  private Long stationId;
}
