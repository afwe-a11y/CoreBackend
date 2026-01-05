package com.tenghe.corebackend.device.infrastructure.persistence.repository;

import com.tenghe.corebackend.device.infrastructure.persistence.json.JsonCodec;
import com.tenghe.corebackend.device.infrastructure.persistence.mapper.DeviceMapper;
import com.tenghe.corebackend.device.infrastructure.persistence.po.DevicePo;
import com.tenghe.corebackend.device.interfaces.DeviceRepositoryPort;
import com.tenghe.corebackend.device.interfaces.portdata.DevicePortData;
import com.tenghe.corebackend.device.model.enums.OnlineStatusEnum;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeviceRepositoryImpl implements DeviceRepositoryPort {
  private final DeviceMapper deviceMapper;
  private final JsonCodec jsonCodec;

  public DeviceRepositoryImpl(DeviceMapper deviceMapper, JsonCodec jsonCodec) {
    this.deviceMapper = deviceMapper;
    this.jsonCodec = jsonCodec;
  }

  @Override
  public DevicePortData save(DevicePortData device) {
    DevicePo po = toPo(device);
    deviceMapper.insert(po);
    return device;
  }

  @Override
  public DevicePortData update(DevicePortData device) {
    DevicePo po = toPo(device);
    deviceMapper.update(po);
    return device;
  }

  @Override
  public DevicePortData findById(Long id) {
    return toModel(deviceMapper.findById(id));
  }

  @Override
  public DevicePortData findByDeviceKey(String deviceKey) {
    return toModel(deviceMapper.findByDeviceKey(deviceKey));
  }

  @Override
  public List<DevicePortData> listAll() {
    return deviceMapper.listAll().stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<DevicePortData> listByProductId(Long productId) {
    return deviceMapper.listByProductId(productId).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<DevicePortData> listByGatewayId(Long gatewayId) {
    return deviceMapper.listByGatewayId(gatewayId).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public List<DevicePortData> search(String keyword, Long productId) {
    String normalized = keyword == null ? null : keyword.trim();
    if (normalized != null && normalized.isEmpty()) {
      normalized = null;
    }
    Long idKeyword = null;
    if (normalized != null) {
      try {
        idKeyword = Long.parseLong(normalized);
      } catch (NumberFormatException ex) {
        idKeyword = null;
      }
    }
    return deviceMapper.search(normalized, productId, idKeyword).stream()
        .map(this::toModel)
        .collect(Collectors.toList());
  }

  @Override
  public long countByProductId(Long productId) {
    return deviceMapper.countByProductId(productId);
  }

  private DevicePortData toModel(DevicePo po) {
    if (po == null) {
      return null;
    }
    DevicePortData device = new DevicePortData();
    device.setId(po.getId());
    device.setName(po.getName());
    device.setProductId(po.getProductId());
    device.setDeviceKey(po.getDeviceKey());
    device.setDeviceSecret(po.getDeviceSecret());
    device.setGatewayId(po.getGatewayId());
    device.setStationId(po.getStationId());
    device.setOnlineStatus(OnlineStatusEnum.fromValue(po.getStatus()));
    device.setDynamicAttributes(jsonCodec.readMap(po.getDynamicAttributes()));
    device.setCreatedAt(po.getCreateTime());
    device.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    return device;
  }

  private DevicePo toPo(DevicePortData device) {
    DevicePo po = new DevicePo();
    po.setId(device.getId());
    po.setName(device.getName());
    po.setProductId(device.getProductId());
    po.setDeviceKey(device.getDeviceKey());
    po.setDeviceSecret(device.getDeviceSecret());
    po.setGatewayId(device.getGatewayId());
    po.setStationId(device.getStationId());
    po.setStatus(device.getOnlineStatus() == null ? null : device.getOnlineStatus().name());
    po.setDynamicAttributes(jsonCodec.writeValue(device.getDynamicAttributes()));
    po.setCreateTime(device.getCreatedAt() == null ? Instant.now() : device.getCreatedAt());
    po.setDeleted(device.isDeleted() ? 1 : 0);
    return po;
  }
}
