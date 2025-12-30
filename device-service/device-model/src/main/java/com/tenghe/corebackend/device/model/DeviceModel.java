package com.tenghe.corebackend.device.model;

import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeviceModel {
    private Long id;
    private String identifier;
    private String name;
    private DeviceModelSource source;
    private Long parentModelId;
    private List<DeviceModelPoint> points;
    private Instant createdAt;
    private boolean deleted;
}
