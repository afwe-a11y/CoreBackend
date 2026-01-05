package com.tenghe.corebackend.device.application.convertor;

import com.tenghe.corebackend.device.api.dto.model.DeviceModelListItem;
import com.tenghe.corebackend.device.interfaces.portdata.DeviceModelPortData;

public class DeviceModelConvertor {

  public static DeviceModelListItem toListItem(DeviceModelPortData model) {
    if (model == null) {
      return null;
    }
    DeviceModelListItem item = new DeviceModelListItem();
    item.setId(model.getId());
    item.setIdentifier(model.getIdentifier());
    item.setName(model.getName());
    item.setSource(model.getSource() == null ? null : model.getSource().name());
    item.setParentModelId(model.getParentModelId());
    item.setPointCount(model.getPoints() == null ? 0 : model.getPoints().size());
    return item;
  }
}
