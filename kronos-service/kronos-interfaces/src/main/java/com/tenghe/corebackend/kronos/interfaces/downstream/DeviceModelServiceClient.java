package com.tenghe.corebackend.kronos.interfaces.downstream;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.CreateDeviceModelRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelDetailResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.ImportDeviceModelPointsRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.UpdateDeviceModelPointsRequest;

/**
 * Device Model Service 下游服务客户端端口。
 * 定义对 Foundation device-service 设备模型接口的调用。
 */
public interface DeviceModelServiceClient {

  /**
   * 分页查询设备模型列表
   */
  DeviceServiceApiResponse<DeviceServicePageResponse<DeviceModelListItem>> listModels(
      Integer page, Integer size);

  /**
   * 查询设备模型详情
   */
  DeviceServiceApiResponse<DeviceModelDetailResponse> getModel(Long modelId);

  /**
   * 创建设备模型
   */
  DeviceServiceApiResponse<DeviceModelDetailResponse> createModel(CreateDeviceModelRequest request);

  /**
   * 删除设备模型
   */
  DeviceServiceApiResponse<Void> deleteModel(Long modelId);

  /**
   * 更新设备模型点位
   */
  DeviceServiceApiResponse<DeviceModelDetailResponse> updatePoints(
      Long modelId, UpdateDeviceModelPointsRequest request);

  /**
   * 导入设备模型点位
   */
  DeviceServiceApiResponse<DeviceModelDetailResponse> importPoints(
      Long modelId, ImportDeviceModelPointsRequest request);
}
