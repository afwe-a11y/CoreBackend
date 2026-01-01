package com.tenghe.corebackend.kronos.controller.converter;

import com.tenghe.corebackend.kronos.api.vo.gateway.GatewayListVO;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.GatewayListItem;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 网关 VO 转换器。
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface GatewayVOConverter {

  GatewayVOConverter INSTANCE = Mappers.getMapper(GatewayVOConverter.class);

  /**
   * 转换为列表 VO
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
  @Mapping(source = "productId", target = "productId", qualifiedByName = "longToString")
  @Mapping(source = "stationId", target = "stationId", qualifiedByName = "longToString")
  GatewayListVO toListVO(GatewayListItem item);

  @Named("longToString")
  default String longToString(Long value) {
    return value == null ? null : String.valueOf(value);
  }
}
