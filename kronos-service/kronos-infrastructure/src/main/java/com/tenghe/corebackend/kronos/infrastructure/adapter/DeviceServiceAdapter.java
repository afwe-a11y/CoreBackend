package com.tenghe.corebackend.kronos.infrastructure.adapter;

import com.tenghe.corebackend.kronos.infrastructure.feign.DeviceFeignClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.DeviceServiceClient;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 设备服务适配器。
 * 封装对 device-service 设备接口的调用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceServiceAdapter implements DeviceServiceClient {

  private final DeviceFeignClient feignClient;

  @Override
  public DeviceServiceApiResponse<DeviceServicePageResponse<DeviceListItem>> listDevices(
      Long productId, String keyword, Integer page, Integer size) {
    log.info("CALL device-service listDevices productId={} keyword={} page={} size={}",
        productId, keyword, page, size);
    long start = System.currentTimeMillis();
    var response = feignClient.listDevices(productId, keyword, page, size);
    log.info("CALL device-service listDevices costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<CreateDeviceResponse> createDevice(CreateDeviceRequest request) {
    log.info("CALL device-service createDevice name={} productId={}",
        request.getName(), request.getProductId());
    long start = System.currentTimeMillis();
    var response = feignClient.createDevice(request);
    log.info("CALL device-service createDevice costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> updateDevice(Long deviceId, UpdateDeviceRequest request) {
    log.info("CALL device-service updateDevice deviceId={}", deviceId);
    long start = System.currentTimeMillis();
    var response = feignClient.updateDevice(deviceId, request);
    log.info("CALL device-service updateDevice costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> deleteDevice(Long deviceId) {
    log.info("CALL device-service deleteDevice deviceId={}", deviceId);
    long start = System.currentTimeMillis();
    var response = feignClient.deleteDevice(deviceId);
    log.info("CALL device-service deleteDevice costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<DeviceImportPreviewResponse> importPreview(
      DeviceImportPreviewRequest request) {
    log.info("CALL device-service importPreview rowsCount={}",
        request.getRows() != null ? request.getRows().size() : 0);
    long start = System.currentTimeMillis();
    var response = feignClient.importPreview(request);
    log.info("CALL device-service importPreview costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<DeviceImportCommitResponse> importCommit(
      DeviceImportCommitRequest request) {
    log.info("CALL device-service importCommit rowsCount={}",
        request.getRows() != null ? request.getRows().size() : 0);
    long start = System.currentTimeMillis();
    var response = feignClient.importCommit(request);
    log.info("CALL device-service importCommit costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<DeviceExportResponse> exportDevices(Long productId, String keyword) {
    log.info("CALL device-service exportDevices productId={} keyword={}", productId, keyword);
    long start = System.currentTimeMillis();
    var response = feignClient.exportDevices(productId, keyword);
    log.info("CALL device-service exportDevices costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<List<DeviceTelemetryItem>> getDeviceTelemetry(Long deviceId) {
    log.info("CALL device-service getDeviceTelemetry deviceId={}", deviceId);
    long start = System.currentTimeMillis();
    var response = feignClient.getDeviceTelemetry(deviceId);
    log.info("CALL device-service getDeviceTelemetry costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }
}
