package com.tenghe.corebackend.kronos.api.vo.device;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备实时数据VO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceRealtimeDataVO {

  /**
   * 设备ID
   */
  @JsonSerialize(using = ToStringSerializer.class)
  private Long deviceId;

  /**
   * 设备名称
   */
  private String deviceName;

  /**
   * 设备状态
   */
  private String status;

  /**
   * 遥测数据（点位标识符 -> 值）
   */
  private Map<String, Object> telemetryData;

  /**
   * 属性数据（点位标识符 -> 值）
   */
  private Map<String, Object> attributeData;

  /**
   * 数据更新时间
   */
  private LocalDateTime dataTime;
}
