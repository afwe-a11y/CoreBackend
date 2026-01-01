package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入提交请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportCommitRequest {

  private List<DeviceImportRowDto> rows;
}
