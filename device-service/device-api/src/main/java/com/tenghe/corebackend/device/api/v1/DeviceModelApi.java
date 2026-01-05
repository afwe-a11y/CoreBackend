package com.tenghe.corebackend.device.api.v1;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.model.CreateDeviceModelRequest;
import com.tenghe.corebackend.device.api.dto.model.DeviceModelDetailResponse;
import com.tenghe.corebackend.device.api.dto.model.DeviceModelListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "设备模型")
public interface DeviceModelApi {

  @Operation(summary = "查询设备模型分页")
  @GetMapping
  ApiResponse<PageResponse<DeviceModelListItem>> listModels(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size);

  @Operation(summary = "查询设备模型详情")
  @GetMapping("/{modelId}")
  ApiResponse<DeviceModelDetailResponse> getModel(@PathVariable("modelId") Long modelId);

  @Operation(summary = "创建设备模型")
  @PostMapping
  ApiResponse<DeviceModelDetailResponse> createModel(@RequestBody CreateDeviceModelRequest request);

  @Operation(summary = "删除设备模型")
  @DeleteMapping("/{modelId}")
  ApiResponse<Void> deleteModel(@PathVariable("modelId") Long modelId);
}
