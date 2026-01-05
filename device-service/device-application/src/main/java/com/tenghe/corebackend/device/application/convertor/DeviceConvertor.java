package com.tenghe.corebackend.device.application.convertor;

import com.tenghe.corebackend.device.api.dto.device.DeviceListItem;
import com.tenghe.corebackend.device.interfaces.portdata.DevicePortData;

public class DeviceConvertor {

  public static DeviceListItem toListItem(DevicePortData device) {
    if (device == null) {
      return null;
    }
    DeviceListItem item = new DeviceListItem();
    item.setId(device.getId());
    item.setName(device.getName());
    item.setProductId(device.getProductId());
    item.setDeviceKey(device.getDeviceKey());
    item.setGatewayId(device.getGatewayId());
    item.setStationId(device.getStationId());
    item.setStatus(device.getOnlineStatus() == null ? null : device.getOnlineStatus().name());
    return item;
  }
}
