package com.tenghe.corebackend.kronos.infrastructure.feign;

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
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 设备 Feign 客户端。
 * 调用 device-service 的设备接口。
 */
@FeignClient(name = "device-service", contextId = "deviceClient", path = "/api/devices")
public interface DeviceFeignClient {

  @GetMapping
  DeviceServiceApiResponse<DeviceServicePageResponse<DeviceListItem>> listDevices(
      @RequestParam(value = "productId", required = false) Long productId,
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size);

  @PostMapping
  DeviceServiceApiResponse<CreateDeviceResponse> createDevice(@RequestBody CreateDeviceRequest request);

  @PutMapping("/{deviceId}")
  DeviceServiceApiResponse<Void> updateDevice(
      @PathVariable("deviceId") Long deviceId,
      @RequestBody UpdateDeviceRequest request);

  @DeleteMapping("/{deviceId}")
  DeviceServiceApiResponse<Void> deleteDevice(@PathVariable("deviceId") Long deviceId);

  @PostMapping("/import/preview")
  DeviceServiceApiResponse<DeviceImportPreviewResponse> importPreview(
      @RequestBody DeviceImportPreviewRequest request);

  @PostMapping("/import/commit")
  DeviceServiceApiResponse<DeviceImportCommitResponse> importCommit(
      @RequestBody DeviceImportCommitRequest request);

  @GetMapping("/export")
  DeviceServiceApiResponse<DeviceExportResponse> exportDevices(
      @RequestParam(value = "productId", required = false) Long productId,
      @RequestParam(value = "keyword", required = false) String keyword);

  @GetMapping("/{deviceId}/telemetry/latest")
  DeviceServiceApiResponse<List<DeviceTelemetryItem>> getDeviceTelemetry(
      @PathVariable("deviceId") Long deviceId);
}
