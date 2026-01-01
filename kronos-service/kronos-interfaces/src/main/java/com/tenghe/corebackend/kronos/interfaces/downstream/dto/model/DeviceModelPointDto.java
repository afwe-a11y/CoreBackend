package com.tenghe.corebackend.kronos.interfaces.downstream.dto.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备模型点位（下游 DTO）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceModelPointDto {

  private String identifier;
  private String name;
  private String type;
  private String dataType;
  private List<String> enumItems;
}
