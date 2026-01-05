package com.tenghe.corebackend.device.infrastructure.persistence.repository;

import com.tenghe.corebackend.device.infrastructure.persistence.mapper.GatewayMapper;
import com.tenghe.corebackend.device.infrastructure.persistence.po.GatewayPo;
import com.tenghe.corebackend.device.interfaces.GatewayRepositoryPort;
import com.tenghe.corebackend.device.interfaces.portdata.GatewayPortData;
import com.tenghe.corebackend.device.model.enums.EnableStatusEnum;
import com.tenghe.corebackend.device.model.enums.OnlineStatusEnum;
import com.tenghe.corebackend.device.model.GatewayType;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class GatewayRepositoryImpl implements GatewayRepositoryPort {
  private final GatewayMapper gatewayMapper;

  public GatewayRepositoryImpl(GatewayMapper gatewayMapper) {
    this.gatewayMapper = gatewayMapper;
  }

  @Override
  public GatewayPortData save(GatewayPortData gateway) {
    GatewayPo po = toPo(gateway);
    gatewayMapper.insert(po);
    return gateway;
  }

  @Override
  public GatewayPortData update(GatewayPortData gateway) {
    GatewayPo po = toPo(gateway);
    gatewayMapper.update(po);
    return gateway;
  }

  @Override
  public GatewayPortData findById(Long id) {
    return toModel(gatewayMapper.findById(id));
  }

  @Override
  public GatewayPortData findBySn(String sn) {
    return toModel(gatewayMapper.findBySn(sn));
  }

  @Override
  public List<GatewayPortData> listAll() {
    return gatewayMapper.listAll().stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<GatewayPortData> searchByNameOrSn(String keyword) {
    return gatewayMapper.searchByNameOrSn(keyword).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  private GatewayPortData toModel(GatewayPo po) {
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
    gateway.setEnableStatus(po.getEnabled() != null && po.getEnabled() == 1 ? EnableStatusEnum.ENABLED : EnableStatusEnum.DISABLED);
    gateway.setCreatedAt(po.getCreateTime());
    gateway.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    return gateway;
  }

  private GatewayPo toPo(GatewayPortData gateway) {
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
