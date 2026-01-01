package com.tenghe.corebackend.kronos.controller.converter;

import com.tenghe.corebackend.kronos.api.dto.device.DeviceImportDTO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceCreateResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceExportResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceExportRowVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceImportCommitResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceImportPreviewResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceListVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceTelemetryVO;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.CreateDeviceResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceExportResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportCommitResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportPreviewResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportRowDto;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceTelemetryItem;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 设备 VO 转换器。
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface DeviceVOConverter {

  DeviceVOConverter INSTANCE = Mappers.getMapper(DeviceVOConverter.class);

  /**
   * 转换为列表 VO
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
  @Mapping(source = "productId", target = "productId", qualifiedByName = "longToString")
  @Mapping(source = "gatewayId", target = "gatewayId", qualifiedByName = "longToString")
  @Mapping(source = "stationId", target = "stationId", qualifiedByName = "longToString")
  DeviceListVO toListVO(DeviceListItem item);

  /**
   * 转换为创建结果 VO
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
  DeviceCreateResultVO toCreateResultVO(CreateDeviceResponse response);

  /**
   * 转换导入 DTO 为下游行 DTO
   */
  DeviceImportRowDto toImportRow(DeviceImportDTO dto);

  /**
   * 批量转换导入行
   */
  default List<DeviceImportRowDto> toImportRows(List<DeviceImportDTO> dtos) {
    if (dtos == null) {
      return null;
    }
    return dtos.stream().map(this::toImportRow).toList();
  }

  /**
   * 转换导入预览结果
   */
  DeviceImportPreviewResultVO toImportPreviewResultVO(DeviceImportPreviewResponse response);

  /**
   * 转换导入提交结果
   */
  DeviceImportCommitResultVO toImportCommitResultVO(DeviceImportCommitResponse response);

  /**
   * 转换导出结果
   */
  @Mapping(source = "rows", target = "rows", qualifiedByName = "toExportRowVOs")
  DeviceExportResultVO toExportResultVO(DeviceExportResponse response);

  /**
   * 转换导出行
   */
  @Mapping(source = "productId", target = "productId", qualifiedByName = "longToString")
  @Mapping(source = "gatewayId", target = "gatewayId", qualifiedByName = "longToString")
  @Mapping(source = "stationId", target = "stationId", qualifiedByName = "longToString")
  DeviceExportRowVO toExportRowVO(DeviceExportResponse.DeviceExportRow row);

  /**
   * 转换遥测数据
   */
  DeviceTelemetryVO toTelemetryVO(DeviceTelemetryItem item);

  /**
   * 批量转换遥测数据
   */
  default List<DeviceTelemetryVO> toTelemetryVOs(List<DeviceTelemetryItem> items) {
    if (items == null) {
      return null;
    }
    return items.stream().map(this::toTelemetryVO).toList();
  }

  @Named("longToString")
  default String longToString(Long value) {
    return value == null ? null : String.valueOf(value);
  }

  @Named("toExportRowVOs")
  default List<DeviceExportRowVO> toExportRowVOs(List<DeviceExportResponse.DeviceExportRow> rows) {
    if (rows == null) {
      return null;
    }
    return rows.stream().map(this::toExportRowVO).toList();
  }
}
