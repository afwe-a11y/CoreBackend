package com.tenghe.corebackend.device.infrastructure.converter.persistence;

import com.tenghe.corebackend.device.infrastructure.persistence.po.GatewayPo;
import com.tenghe.corebackend.device.interfaces.portdata.GatewayPortData;
import com.tenghe.corebackend.device.model.GatewayType;
import com.tenghe.corebackend.device.model.enums.EnableStatusEnum;
import com.tenghe.corebackend.device.model.enums.OnlineStatusEnum;

import java.time.Instant;

public class GatewayPersistenceConverter {

  public static GatewayPortData toModel(GatewayPo po) {
    if (po == null) {
      return null;
    }
    GatewayPortData gateway = new GatewayPortData();
    gateway.setId(po.getId());
    gateway.setName(po.getName());
    gateway.setType(GatewayType.fromValue(po.getType()));
    gateway.setSn(po.getSn());
    gateway.setProductId(po.getProductId());
    gateway.setStationId(po.getStationId());
    gateway.setOnlineStatus(OnlineStatusEnum.fromValue(po.getStatus()));
    gateway.setEnableStatus(po.getEnabled() != null && po.getEnabled() == 1 
        ? EnableStatusEnum.ENABLED : EnableStatusEnum.DISABLED);
    gateway.setCreatedAt(po.getCreateTime());
    gateway.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    return gateway;
  }

  public static GatewayPo toPO(GatewayPortData gateway) {
    if (gateway == null) {
      return null;
    }
    GatewayPo po = new GatewayPo();
    po.setId(gateway.getId());
    po.setName(gateway.getName());
    po.setType(gateway.getType() == null ? null : gateway.getType().name());
    po.setSn(gateway.getSn());
    po.setProductId(gateway.getProductId());
    po.setStationId(gateway.getStationId());
    po.setStatus(gateway.getOnlineStatus() == null ? null : gateway.getOnlineStatus().name());
    po.setEnabled(gateway.getEnableStatus() == EnableStatusEnum.ENABLED ? 1 : 0);
    po.setCreateTime(gateway.getCreatedAt() == null ? Instant.now() : gateway.getCreatedAt());
    po.setDeleted(gateway.isDeleted() ? 1 : 0);
    return po;
  }
}
