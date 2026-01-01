package com.tenghe.corebackend.kronos.interfaces.downstream;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.CreateDeviceRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.CreateDeviceResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceExportResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportCommitRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportCommitResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportPreviewRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportPreviewResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceTelemetryItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.UpdateDeviceRequest;
import java.util.List;

/**
 * Device Service 下游服务客户端端口。
 * 定义对 Foundation device-service 的调用接口。
 */
public interface DeviceServiceClient {

  /**
   * 分页查询设备列表
   */
  DeviceServiceApiResponse<DeviceServicePageResponse<DeviceListItem>> listDevices(
      Long productId, String keyword, Integer page, Integer size);

  /**
   * 创建设备
   */
  DeviceServiceApiResponse<CreateDeviceResponse> createDevice(CreateDeviceRequest request);

  /**
   * 更新设备
   */
  DeviceServiceApiResponse<Void> updateDevice(Long deviceId, UpdateDeviceRequest request);

  /**
   * 删除设备
   */
  DeviceServiceApiResponse<Void> deleteDevice(Long deviceId);

  /**
   * 设备导入预览
   */
  DeviceServiceApiResponse<DeviceImportPreviewResponse> importPreview(DeviceImportPreviewRequest request);

  /**
   * 设备导入提交
   */
  DeviceServiceApiResponse<DeviceImportCommitResponse> importCommit(DeviceImportCommitRequest request);

  /**
   * 设备导出
   */
  DeviceServiceApiResponse<DeviceExportResponse> exportDevices(Long productId, String keyword);

  /**
   * 获取设备遥测数据
   */
  DeviceServiceApiResponse<List<DeviceTelemetryItem>> getDeviceTelemetry(Long deviceId);
}
