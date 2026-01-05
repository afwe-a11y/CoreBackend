package com.tenghe.corebackend.device.api.v1;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.device.CreateDeviceRequest;
import com.tenghe.corebackend.device.api.dto.device.CreateDeviceResponse;
import com.tenghe.corebackend.device.api.dto.device.DeviceListItem;
import com.tenghe.corebackend.device.api.dto.device.UpdateDeviceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备")
public interface DeviceApi {

  @Operation(summary = "查询设备分页")
  @GetMapping
  ApiResponse<PageResponse<DeviceListItem>> listDevices(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "productId", required = false) Long productId,
      @RequestParam(value = "keyword", required = false) String keyword);

  @Operation(summary = "创建设备")
  @PostMapping
  ApiResponse<CreateDeviceResponse> createDevice(@RequestBody CreateDeviceRequest request);

  @Operation(summary = "更新设备")
  @PutMapping("/{deviceId}")
  ApiResponse<Void> updateDevice(
      @PathVariable("deviceId") Long deviceId,
      @RequestBody UpdateDeviceRequest request);

  @Operation(summary = "删除设备")
  @DeleteMapping("/{deviceId}")
  ApiResponse<Void> deleteDevice(@PathVariable("deviceId") Long deviceId);
}
