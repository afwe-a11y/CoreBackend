package com.tenghe.corebackend.kronos.infrastructure.adapter;

import com.tenghe.corebackend.kronos.infrastructure.feign.DeviceModelFeignClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.DeviceModelServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.CreateDeviceModelRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelDetailResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.ImportDeviceModelPointsRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.UpdateDeviceModelPointsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 设备模型服务适配器。
 * 封装对 device-service 设备模型接口的调用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceModelServiceAdapter implements DeviceModelServiceClient {

  private final DeviceModelFeignClient feignClient;

  @Override
  public DeviceServiceApiResponse<DeviceServicePageResponse<DeviceModelListItem>> listModels(
      Integer page, Integer size) {
    log.info("CALL device-service listModels page={} size={}", page, size);
    long start = System.currentTimeMillis();
    var response = feignClient.listModels(page, size);
    log.info("CALL device-service listModels costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<DeviceModelDetailResponse> getModel(Long modelId) {
    log.info("CALL device-service getModel modelId={}", modelId);
    long start = System.currentTimeMillis();
    var response = feignClient.getModel(modelId);
    log.info("CALL device-service getModel costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<DeviceModelDetailResponse> createModel(CreateDeviceModelRequest request) {
    log.info("CALL device-service createModel identifier={}", request.getIdentifier());
    long start = System.currentTimeMillis();
    var response = feignClient.createModel(request);
    log.info("CALL device-service createModel costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> deleteModel(Long modelId) {
    log.info("CALL device-service deleteModel modelId={}", modelId);
    long start = System.currentTimeMillis();
    var response = feignClient.deleteModel(modelId);
    log.info("CALL device-service deleteModel costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<DeviceModelDetailResponse> updatePoints(
      Long modelId, UpdateDeviceModelPointsRequest request) {
    log.info("CALL device-service updatePoints modelId={}", modelId);
    long start = System.currentTimeMillis();
    var response = feignClient.updatePoints(modelId, request);
    log.info("CALL device-service updatePoints costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<DeviceModelDetailResponse> importPoints(
      Long modelId, ImportDeviceModelPointsRequest request) {
    log.info("CALL device-service importPoints modelId={} pointsCount={}",
        modelId, request.getPoints() != null ? request.getPoints().size() : 0);
    long start = System.currentTimeMillis();
    var response = feignClient.importPoints(modelId, request);
    log.info("CALL device-service importPoints costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }
}
