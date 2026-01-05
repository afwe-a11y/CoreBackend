package com.tenghe.corebackend.device.controller.web.converter;

import com.tenghe.corebackend.device.api.dto.device.*;
import com.tenghe.corebackend.device.application.command.CreateDeviceCommand;
import com.tenghe.corebackend.device.application.command.DeviceImportRowCommand;
import com.tenghe.corebackend.device.application.command.UpdateDeviceCommand;
import com.tenghe.corebackend.device.application.service.result.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Device DTO ↔ Command/Result 转换器
 */
public class DeviceConverter {

    public static CreateDeviceCommand toCommand(CreateDeviceRequest request) {
        CreateDeviceCommand command = new CreateDeviceCommand();
        command.setName(request.getName());
        command.setProductId(request.getProductId());
        command.setDeviceKey(request.getDeviceKey());
        command.setGatewayId(request.getGatewayId());
        command.setDynamicAttributes(request.getDynamicAttributes());
        return command;
    }

    public static UpdateDeviceCommand toCommand(Long deviceId, UpdateDeviceRequest request) {
        UpdateDeviceCommand command = new UpdateDeviceCommand();
        command.setDeviceId(deviceId);
        command.setName(request.getName());
        command.setGatewayId(request.getGatewayId());
        command.setDynamicAttributes(request.getDynamicAttributes());
        return command;
    }

    public static List<DeviceImportRowCommand> toImportCommands(List<DeviceImportRowDto> rows) {
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

    public static CreateDeviceResponse toResponse(CreateDeviceResult result) {
        return new CreateDeviceResponse(result.getId(), result.getDeviceSecret());
    }

    public static DeviceListItem toListItem(DeviceListItemResult result) {
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

    public static DeviceTelemetryItem toTelemetryItem(DeviceTelemetryResult result) {
        DeviceTelemetryItem item = new DeviceTelemetryItem();
        item.setPointIdentifier(result.getPointIdentifier());
        item.setValue(result.getValue());
        item.setUpdatedAt(result.getUpdatedAt() == null ? null : result.getUpdatedAt().toString());
        return item;
    }

    public static DeviceImportPreviewResponse toPreviewResponse(DeviceImportPreviewResult result) {
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
        return response;
    }

    public static DeviceImportCommitResponse toCommitResponse(DeviceImportCommitResult result) {
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
        return response;
    }

    public static DeviceExportResponse toExportResponse(DeviceExportResult result) {
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
        return response;
    }
}
