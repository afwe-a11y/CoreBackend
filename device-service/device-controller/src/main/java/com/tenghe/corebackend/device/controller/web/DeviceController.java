package com.tenghe.corebackend.device.controller.web;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.device.CreateDeviceRequest;
import com.tenghe.corebackend.device.api.dto.device.CreateDeviceResponse;
import com.tenghe.corebackend.device.api.dto.device.DeviceExportResponse;
import com.tenghe.corebackend.device.api.dto.device.DeviceExportRow;
import com.tenghe.corebackend.device.api.dto.device.DeviceImportCommitRequest;
import com.tenghe.corebackend.device.api.dto.device.DeviceImportCommitResponse;
import com.tenghe.corebackend.device.api.dto.device.DeviceImportFailure;
import com.tenghe.corebackend.device.api.dto.device.DeviceImportPreviewItem;
import com.tenghe.corebackend.device.api.dto.device.DeviceImportPreviewRequest;
import com.tenghe.corebackend.device.api.dto.device.DeviceImportPreviewResponse;
import com.tenghe.corebackend.device.api.dto.device.DeviceImportRowDto;
import com.tenghe.corebackend.device.api.dto.device.DeviceListItem;
import com.tenghe.corebackend.device.api.dto.device.DeviceTelemetryItem;
import com.tenghe.corebackend.device.api.dto.device.UpdateDeviceRequest;
import com.tenghe.corebackend.device.application.command.CreateDeviceCommand;
import com.tenghe.corebackend.device.application.command.DeviceImportRowCommand;
import com.tenghe.corebackend.device.application.command.UpdateDeviceCommand;
import com.tenghe.corebackend.device.application.service.DeviceApplicationService;
import com.tenghe.corebackend.device.application.service.result.CreateDeviceResult;
import com.tenghe.corebackend.device.application.service.result.DeviceExportResult;
import com.tenghe.corebackend.device.application.service.result.DeviceExportRowResult;
import com.tenghe.corebackend.device.application.service.result.DeviceImportCommitResult;
import com.tenghe.corebackend.device.application.service.result.DeviceImportFailureResult;
import com.tenghe.corebackend.device.application.service.result.DeviceImportPreviewItemResult;
import com.tenghe.corebackend.device.application.service.result.DeviceImportPreviewResult;
import com.tenghe.corebackend.device.application.service.result.DeviceListItemResult;
import com.tenghe.corebackend.device.application.service.result.DeviceTelemetryResult;
import com.tenghe.corebackend.device.application.service.result.PageResult;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceApplicationService deviceService;

    public DeviceController(DeviceApplicationService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ApiResponse<PageResponse<DeviceListItem>> listDevices(
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        PageResult<DeviceListItemResult> result = deviceService.listDevices(productId, keyword, page, size);
        List<DeviceListItem> items = result.getItems().stream()
                .map(this::toListItem)
                .collect(Collectors.toList());
        return ApiResponse.ok(new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize()));
    }

    @PostMapping
    public ApiResponse<CreateDeviceResponse> createDevice(@RequestBody CreateDeviceRequest request) {
        CreateDeviceCommand command = new CreateDeviceCommand();
        command.setName(request.getName());
        command.setProductId(request.getProductId());
        command.setDeviceKey(request.getDeviceKey());
        command.setGatewayId(request.getGatewayId());
        command.setDynamicAttributes(request.getDynamicAttributes());
        CreateDeviceResult result = deviceService.createDevice(command);
        return ApiResponse.ok(new CreateDeviceResponse(result.getId(), result.getDeviceSecret()));
    }

    @PutMapping("/{deviceId}")
    public ApiResponse<Void> updateDevice(
            @PathVariable("deviceId") Long deviceId,
            @RequestBody UpdateDeviceRequest request) {
        UpdateDeviceCommand command = new UpdateDeviceCommand();
        command.setDeviceId(deviceId);
        command.setName(request.getName());
        command.setGatewayId(request.getGatewayId());
        command.setDynamicAttributes(request.getDynamicAttributes());
        deviceService.updateDevice(command);
        return ApiResponse.ok(null);
    }

    @PostMapping("/import/preview")
    public ApiResponse<DeviceImportPreviewResponse> previewImport(@RequestBody DeviceImportPreviewRequest request) {
        DeviceImportPreviewResult result = deviceService.previewImport(toImportCommands(request.getRows()));
        DeviceImportPreviewResponse response = new DeviceImportPreviewResponse();
        response.setTotal(result.getTotal());
        response.setCreateCount(result.getCreateCount());
        response.setUpdateCount(result.getUpdateCount());
        response.setInvalidCount(result.getInvalidCount());
        List<DeviceImportPreviewItem> items = new ArrayList<>();
        if (result.getItems() != null) {
            for (DeviceImportPreviewItemResult itemResult : result.getItems()) {
                DeviceImportPreviewItem item = new DeviceImportPreviewItem();
                item.setRowIndex(itemResult.getRowIndex());
                item.setAction(itemResult.getAction());
                item.setMessage(itemResult.getMessage());
                items.add(item);
            }
        }
        response.setItems(items);
        return ApiResponse.ok(response);
    }

    @PostMapping("/import/commit")
    public ApiResponse<DeviceImportCommitResponse> commitImport(@RequestBody DeviceImportCommitRequest request) {
        DeviceImportCommitResult result = deviceService.commitImport(toImportCommands(request.getRows()));
        DeviceImportCommitResponse response = new DeviceImportCommitResponse();
        response.setSuccessCount(result.getSuccessCount());
        response.setFailureCount(result.getFailureCount());
        List<DeviceImportFailure> failures = new ArrayList<>();
        if (result.getFailures() != null) {
            for (DeviceImportFailureResult failureResult : result.getFailures()) {
                DeviceImportFailure failure = new DeviceImportFailure();
                failure.setRowIndex(failureResult.getRowIndex());
                failure.setMessage(failureResult.getMessage());
                failures.add(failure);
            }
        }
        response.setFailures(failures);
        return ApiResponse.ok(response);
    }

    @GetMapping("/export")
    public ApiResponse<DeviceExportResponse> exportDevices(
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        DeviceExportResult result = deviceService.exportDevices(productId, keyword);
        DeviceExportResponse response = new DeviceExportResponse();
        response.setFileName(result.getFileName());
        List<DeviceExportRow> rows = new ArrayList<>();
        if (result.getRows() != null) {
            for (DeviceExportRowResult rowResult : result.getRows()) {
                DeviceExportRow row = new DeviceExportRow();
                row.setName(rowResult.getName());
                row.setDeviceKey(rowResult.getDeviceKey());
                row.setProductId(rowResult.getProductId());
                row.setGatewayId(rowResult.getGatewayId());
                row.setStationId(rowResult.getStationId());
                rows.add(row);
            }
        }
        response.setRows(rows);
        return ApiResponse.ok(response);
    }

    @GetMapping("/{deviceId}/telemetry/latest")
    public ApiResponse<List<DeviceTelemetryItem>> listLatestTelemetry(@PathVariable("deviceId") Long deviceId) {
        List<DeviceTelemetryResult> results = deviceService.listLatestTelemetry(deviceId);
        List<DeviceTelemetryItem> items = results.stream()
                .map(this::toTelemetryItem)
                .collect(Collectors.toList());
        return ApiResponse.ok(items);
    }

    private DeviceListItem toListItem(DeviceListItemResult result) {
        DeviceListItem item = new DeviceListItem();
        item.setId(result.getId());
        item.setName(result.getName());
        item.setProductId(result.getProductId());
        item.setDeviceKey(result.getDeviceKey());
        item.setGatewayId(result.getGatewayId());
        item.setStationId(result.getStationId());
        item.setStatus(result.getStatus());
        return item;
    }

    private List<DeviceImportRowCommand> toImportCommands(List<DeviceImportRowDto> rows) {
        if (rows == null) {
            return null;
        }
        List<DeviceImportRowCommand> commands = new ArrayList<>();
        for (DeviceImportRowDto row : rows) {
            DeviceImportRowCommand command = new DeviceImportRowCommand();
            command.setId(row.getId());
            command.setName(row.getName());
            command.setProductId(row.getProductId());
            command.setDeviceKey(row.getDeviceKey());
            command.setGatewayId(row.getGatewayId());
            command.setDynamicAttributes(row.getDynamicAttributes());
            commands.add(command);
        }
        return commands;
    }

    private DeviceTelemetryItem toTelemetryItem(DeviceTelemetryResult result) {
        DeviceTelemetryItem item = new DeviceTelemetryItem();
        item.setPointIdentifier(result.getPointIdentifier());
        item.setValue(result.getValue());
        item.setUpdatedAt(result.getUpdatedAt() == null ? null : result.getUpdatedAt().toString());
        return item;
    }
}
