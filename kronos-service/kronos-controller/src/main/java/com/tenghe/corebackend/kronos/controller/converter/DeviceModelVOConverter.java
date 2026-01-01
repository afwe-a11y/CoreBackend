package com.tenghe.corebackend.kronos.controller.converter;

import com.tenghe.corebackend.kronos.api.dto.devicemodel.ModelPointImportDTO;
import com.tenghe.corebackend.kronos.api.vo.devicemodel.DeviceModelDetailVO;
import com.tenghe.corebackend.kronos.api.vo.devicemodel.DeviceModelListVO;
import com.tenghe.corebackend.kronos.api.vo.devicemodel.ModelPointVO;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelDetailResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelPointDto;
import java.util.List;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 设备模型 VO 转换器。
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface DeviceModelVOConverter {

  DeviceModelVOConverter INSTANCE = Mappers.getMapper(DeviceModelVOConverter.class);

  /**
   * 转换为列表 VO
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
  @Mapping(source = "parentModelId", target = "parentModelId", qualifiedByName = "longToString")
  DeviceModelListVO toListVO(DeviceModelListItem item);

  /**
   * 转换为详情 VO
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
  @Mapping(source = "parentModelId", target = "parentModelId", qualifiedByName = "longToString")
  @Mapping(source = "points", target = "points", qualifiedByName = "toPointVOs")
  DeviceModelDetailVO toDetailVO(DeviceModelDetailResponse response);

  /**
   * 转换为点位 VO
   */
  ModelPointVO toPointVO(DeviceModelPointDto dto);

  /**
   * 转换为下游点位 DTO
   */
  DeviceModelPointDto toPointDto(ModelPointImportDTO dto);

  /**
   * 批量转换点位
   */
  default List<DeviceModelPointDto> toPointDtos(List<ModelPointImportDTO> dtos) {
    if (dtos == null) {
      return null;
    }
    return dtos.stream().map(this::toPointDto).toList();
  }

  @Named("longToString")
  default String longToString(Long value) {
    return value == null ? null : String.valueOf(value);
  }

  @Named("toPointVOs")
  default List<ModelPointVO> toPointVOs(List<DeviceModelPointDto> dtos) {
    if (dtos == null) {
      return null;
    }
    return dtos.stream().map(this::toPointVO).toList();
  }
}
