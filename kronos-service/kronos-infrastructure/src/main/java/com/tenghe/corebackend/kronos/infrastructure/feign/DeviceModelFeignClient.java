package com.tenghe.corebackend.kronos.infrastructure.feign;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.CreateDeviceModelRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelDetailResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.ImportDeviceModelPointsRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.UpdateDeviceModelPointsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 设备模型 Feign 客户端。
 * 调用 device-service 的设备模型接口。
 */
@FeignClient(name = "device-service", contextId = "deviceModelClient", path = "/api/device-models")
public interface DeviceModelFeignClient {

  @GetMapping
  DeviceServiceApiResponse<DeviceServicePageResponse<DeviceModelListItem>> listModels(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size);

  @GetMapping("/{modelId}")
  DeviceServiceApiResponse<DeviceModelDetailResponse> getModel(@PathVariable("modelId") Long modelId);

  @PostMapping
  DeviceServiceApiResponse<DeviceModelDetailResponse> createModel(@RequestBody CreateDeviceModelRequest request);

  @DeleteMapping("/{modelId}")
  DeviceServiceApiResponse<Void> deleteModel(@PathVariable("modelId") Long modelId);

  @PutMapping("/{modelId}/points")
  DeviceServiceApiResponse<DeviceModelDetailResponse> updatePoints(
      @PathVariable("modelId") Long modelId,
      @RequestBody UpdateDeviceModelPointsRequest request);

  @PostMapping("/{modelId}/points/import")
  DeviceServiceApiResponse<DeviceModelDetailResponse> importPoints(
      @PathVariable("modelId") Long modelId,
      @RequestBody ImportDeviceModelPointsRequest request);
}
