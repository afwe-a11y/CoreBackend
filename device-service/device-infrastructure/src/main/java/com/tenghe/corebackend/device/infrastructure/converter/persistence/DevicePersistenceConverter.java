package com.tenghe.corebackend.device.infrastructure.converter.persistence;

import com.tenghe.corebackend.device.infrastructure.persistence.po.DevicePo;
import com.tenghe.corebackend.device.interfaces.portdata.DevicePortData;
import com.tenghe.corebackend.device.model.enums.OnlineStatusEnum;

import java.time.Instant;
import java.util.Map;

public class DevicePersistenceConverter {

  public static DevicePortData toModel(DevicePo po, Map<String, Object> dynamicAttributes) {
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
    device.setDynamicAttributes(dynamicAttributes);
    device.setCreatedAt(po.getCreateTime());
    device.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
    return device;
  }

  public static DevicePo toPO(DevicePortData device, String dynamicAttributesJson) {
    if (device == null) {
      return null;
    }
    DevicePo po = new DevicePo();
    po.setId(device.getId());
    po.setName(device.getName());
    po.setProductId(device.getProductId());
    po.setDeviceKey(device.getDeviceKey());
    po.setDeviceSecret(device.getDeviceSecret());
    po.setGatewayId(device.getGatewayId());
    po.setStationId(device.getStationId());
    po.setStatus(device.getOnlineStatus() == null ? null : device.getOnlineStatus().name());
    po.setDynamicAttributes(dynamicAttributesJson);
    po.setCreateTime(device.getCreatedAt() == null ? Instant.now() : device.getCreatedAt());
    po.setDeleted(device.isDeleted() ? 1 : 0);
    return po;
  }
}
