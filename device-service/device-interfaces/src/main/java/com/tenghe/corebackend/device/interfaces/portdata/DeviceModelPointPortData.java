package com.tenghe.corebackend.device.interfaces.portdata;

import com.tenghe.corebackend.device.model.DeviceDataType;
import com.tenghe.corebackend.device.model.enums.PointTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeviceModelPointPortData {
  private String identifier;
  private String name;
  private PointTypeEnum pointType;
  private DeviceDataType dataType;
  private List<String> enumItems;
}
