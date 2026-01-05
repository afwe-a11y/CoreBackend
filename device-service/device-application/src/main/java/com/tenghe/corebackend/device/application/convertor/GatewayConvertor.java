package com.tenghe.corebackend.device.application.convertor;

import com.tenghe.corebackend.device.api.dto.gateway.GatewayListItem;
import com.tenghe.corebackend.device.interfaces.portdata.GatewayPortData;
import com.tenghe.corebackend.device.model.enums.EnableStatusEnum;

public class GatewayConvertor {

  public static GatewayListItem toListItem(GatewayPortData gateway) {
    if (gateway == null) {
      return null;
    }
    GatewayListItem item = new GatewayListItem();
    item.setId(gateway.getId());
    item.setName(gateway.getName());
    item.setType(gateway.getType() == null ? null : gateway.getType().name());
    item.setSn(gateway.getSn());
    item.setProductId(gateway.getProductId());
    item.setStationId(gateway.getStationId());
    item.setStatus(gateway.getOnlineStatus() == null ? null : gateway.getOnlineStatus().name());
    item.setEnabled(gateway.getEnableStatus() == EnableStatusEnum.ENABLED);
    return item;
  }
}
