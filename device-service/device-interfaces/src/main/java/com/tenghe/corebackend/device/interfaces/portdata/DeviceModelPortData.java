package com.tenghe.corebackend.device.interfaces.portdata;

import com.tenghe.corebackend.device.model.DeviceModelSource;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class DeviceModelPortData {
  private Long id;
  private String identifier;
  private String name;
  private DeviceModelSource source;
  private Long parentModelId;
  private List<DeviceModelPointPortData> points;
  private Instant createdAt;
  private boolean deleted;
}
