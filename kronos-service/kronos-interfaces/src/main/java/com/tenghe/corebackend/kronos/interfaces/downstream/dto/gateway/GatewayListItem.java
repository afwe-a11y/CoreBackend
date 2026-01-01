package com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关列表项（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GatewayListItem {

  private Long id;
  private String name;
  private String type;
  private String sn;
  private Long productId;
  private Long stationId;
  private String status;
  private Boolean enabled;
}
