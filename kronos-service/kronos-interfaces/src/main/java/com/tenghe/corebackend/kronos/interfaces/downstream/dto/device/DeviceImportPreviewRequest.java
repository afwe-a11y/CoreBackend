package com.tenghe.corebackend.kronos.interfaces.downstream.dto.device;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 设备导入预览请求（下游 DTO）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceImportPreviewRequest {

  private List<DeviceImportRowDto> rows;
}
