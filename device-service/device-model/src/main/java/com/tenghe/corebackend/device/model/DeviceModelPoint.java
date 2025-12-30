package com.tenghe.corebackend.device.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceModelPoint {
    private String identifier;
    private DevicePointType type;
    private DeviceDataType dataType;
    private List<String> enumItems;
}
