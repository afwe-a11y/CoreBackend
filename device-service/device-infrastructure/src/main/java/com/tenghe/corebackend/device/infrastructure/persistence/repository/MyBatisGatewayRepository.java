package com.tenghe.corebackend.device.infrastructure.persistence.repository;

import com.tenghe.corebackend.device.infrastructure.persistence.mapper.GatewayMapper;
import com.tenghe.corebackend.device.infrastructure.persistence.po.GatewayPo;
import com.tenghe.corebackend.device.interfaces.GatewayRepositoryPort;
import com.tenghe.corebackend.device.model.Gateway;
import com.tenghe.corebackend.device.model.GatewayStatus;
import com.tenghe.corebackend.device.model.GatewayType;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisGatewayRepository implements GatewayRepositoryPort {
    private final GatewayMapper gatewayMapper;

    public MyBatisGatewayRepository(GatewayMapper gatewayMapper) {
        this.gatewayMapper = gatewayMapper;
    }

    @Override
    public Gateway save(Gateway gateway) {
        GatewayPo po = toPo(gateway);
        gatewayMapper.insert(po);
        return gateway;
    }

    @Override
    public Gateway update(Gateway gateway) {
        GatewayPo po = toPo(gateway);
        gatewayMapper.update(po);
        return gateway;
    }

    @Override
    public Gateway findById(Long id) {
        return toModel(gatewayMapper.findById(id));
    }

    @Override
    public Gateway findBySn(String sn) {
        return toModel(gatewayMapper.findBySn(sn));
    }

    @Override
    public List<Gateway> listAll() {
        return gatewayMapper.listAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Gateway> searchByNameOrSn(String keyword) {
        return gatewayMapper.searchByNameOrSn(keyword).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    private Gateway toModel(GatewayPo po) {
        if (po == null) {
            return null;
        }
        Gateway gateway = new Gateway();
        gateway.setId(po.getId());
        gateway.setName(po.getName());
        gateway.setType(GatewayType.fromValue(po.getType()));
        gateway.setSn(po.getSn());
        gateway.setProductId(po.getProductId());
        gateway.setStationId(po.getStationId());
        gateway.setStatus(GatewayStatus.fromValue(po.getStatus()));
        gateway.setEnabled(po.getEnabled() != null && po.getEnabled() == 1);
        gateway.setCreatedAt(po.getCreateTime());
        gateway.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
        return gateway;
    }

    private GatewayPo toPo(Gateway gateway) {
        GatewayPo po = new GatewayPo();
        po.setId(gateway.getId());
        po.setName(gateway.getName());
        po.setType(gateway.getType() == null ? null : gateway.getType().name());
        po.setSn(gateway.getSn());
        po.setProductId(gateway.getProductId());
        po.setStationId(gateway.getStationId());
        po.setStatus(gateway.getStatus() == null ? null : gateway.getStatus().name());
        po.setEnabled(gateway.isEnabled() ? 1 : 0);
        po.setCreateTime(gateway.getCreatedAt() == null ? Instant.now() : gateway.getCreatedAt());
        po.setDeleted(gateway.isDeleted() ? 1 : 0);
        return po;
    }
}
