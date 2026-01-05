package com.tenghe.corebackend.device.controller.web;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.model.*;
import com.tenghe.corebackend.device.application.command.CreateDeviceModelCommand;
import com.tenghe.corebackend.device.application.command.DeviceModelPointCommand;
import com.tenghe.corebackend.device.application.command.ImportDeviceModelPointsCommand;
import com.tenghe.corebackend.device.application.command.UpdateDeviceModelPointsCommand;
import com.tenghe.corebackend.device.application.service.DeviceModelApplicationService;
import com.tenghe.corebackend.device.application.service.result.DeviceModelDetailResult;
import com.tenghe.corebackend.device.application.service.result.DeviceModelListItemResult;
import com.tenghe.corebackend.device.application.service.result.DeviceModelPointResult;
import com.tenghe.corebackend.device.application.service.result.PageResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/device-models")
public class DeviceModelController {
  private final DeviceModelApplicationService deviceModelService;

  public DeviceModelController(DeviceModelApplicationService deviceModelService) {
    this.deviceModelService = deviceModelService;
  }

  @GetMapping
  public ApiResponse<PageResponse<DeviceModelListItem>> listModels(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    PageResult<DeviceModelListItemResult> result = deviceModelService.listModels(page, size);
    List<DeviceModelListItem> items = result.getItems().stream()
        .map(this::toListItem)
        .collect(Collectors.toList());
    return ApiResponse.ok(new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize()));
  }

  @GetMapping("/{modelId}")
  public ApiResponse<DeviceModelDetailResponse> getModel(@PathVariable("modelId") Long modelId) {
    DeviceModelDetailResult result = deviceModelService.getModel(modelId);
    return ApiResponse.ok(toDetail(result));
  }

  @PostMapping
  public ApiResponse<DeviceModelDetailResponse> createModel(@RequestBody CreateDeviceModelRequest request) {
    CreateDeviceModelCommand command = new CreateDeviceModelCommand();
    command.setIdentifier(request.getIdentifier());
    command.setName(request.getName());
    command.setSource(request.getSource());
    command.setParentModelId(request.getParentModelId());
    command.setPoints(toPointCommands(request.getPoints()));
    DeviceModelDetailResult result = deviceModelService.createModel(command);
    return ApiResponse.ok(toDetail(result));
  }

  @DeleteMapping("/{modelId}")
  public ApiResponse<Void> deleteModel(@PathVariable("modelId") Long modelId) {
    deviceModelService.deleteModel(modelId);
    return ApiResponse.ok(null);
  }

  @PutMapping("/{modelId}/points")
  public ApiResponse<DeviceModelDetailResponse> updatePoints(
      @PathVariable("modelId") Long modelId,
      @RequestBody UpdateDeviceModelPointsRequest request) {
    UpdateDeviceModelPointsCommand command = new UpdateDeviceModelPointsCommand();
    command.setModelId(modelId);
    command.setPoints(toPointCommands(request.getPoints()));
    DeviceModelDetailResult result = deviceModelService.updatePoints(command);
    return ApiResponse.ok(toDetail(result));
  }

  @PostMapping("/{modelId}/points/import")
  public ApiResponse<DeviceModelDetailResponse> importPoints(
      @PathVariable("modelId") Long modelId,
      @RequestBody ImportDeviceModelPointsRequest request) {
    ImportDeviceModelPointsCommand command = new ImportDeviceModelPointsCommand();
    command.setModelId(modelId);
    command.setPoints(toPointCommands(request.getPoints()));
    DeviceModelDetailResult result = deviceModelService.importPoints(command);
    return ApiResponse.ok(toDetail(result));
  }

  private DeviceModelListItem toListItem(DeviceModelListItemResult result) {
    DeviceModelListItem item = new DeviceModelListItem();
    item.setId(result.getId());
    item.setIdentifier(result.getIdentifier());
    item.setName(result.getName());
    item.setSource(result.getSource());
    item.setParentModelId(result.getParentModelId());
    item.setPointCount(result.getPointCount());
    return item;
  }

  private DeviceModelDetailResponse toDetail(DeviceModelDetailResult result) {
    DeviceModelDetailResponse response = new DeviceModelDetailResponse();
    response.setId(result.getId());
    response.setIdentifier(result.getIdentifier());
    response.setName(result.getName());
    response.setSource(result.getSource());
    response.setParentModelId(result.getParentModelId());
    List<DeviceModelPointDto> points = new ArrayList<>();
    if (result.getPoints() != null) {
      for (DeviceModelPointResult point : result.getPoints()) {
        DeviceModelPointDto dto = new DeviceModelPointDto();
        dto.setIdentifier(point.getIdentifier());
        dto.setName(point.getName());
        dto.setType(point.getType());
        dto.setDataType(point.getDataType());
        dto.setEnumItems(point.getEnumItems());
        points.add(dto);
      }
    }
    response.setPoints(points);
    return response;
  }

  private List<DeviceModelPointCommand> toPointCommands(List<DeviceModelPointDto> dtos) {
    if (dtos == null) {
      return null;
    }
    List<DeviceModelPointCommand> commands = new ArrayList<>();
    for (DeviceModelPointDto dto : dtos) {
      DeviceModelPointCommand command = new DeviceModelPointCommand();
      command.setIdentifier(dto.getIdentifier());
      command.setName(dto.getName());
      command.setType(dto.getType());
      command.setDataType(dto.getDataType());
      command.setEnumItems(dto.getEnumItems());
      commands.add(command);
    }
    return commands;
  }
}
