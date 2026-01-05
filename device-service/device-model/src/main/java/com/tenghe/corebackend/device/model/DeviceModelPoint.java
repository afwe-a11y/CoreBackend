package com.tenghe.corebackend.device.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeviceModelPoint {
  private String identifier;
  private String name;
  private DevicePointType type;
  private DeviceDataType dataType;
  private List<String> enumItems;
}
