package com.tenghe.corebackend.kronos.controller.converter;

import com.tenghe.corebackend.kronos.api.vo.product.ProductCreateResultVO;
import com.tenghe.corebackend.kronos.api.vo.product.ProductListVO;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.ProductListItem;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

/**
 * 产品 VO 转换器。
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface ProductVOConverter {

  ProductVOConverter INSTANCE = Mappers.getMapper(ProductVOConverter.class);

  /**
   * 转换为列表 VO
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
  @Mapping(source = "deviceModelId", target = "deviceModelId", qualifiedByName = "longToString")
  ProductListVO toListVO(ProductListItem item);

  /**
   * 转换为创建结果 VO
   */
  @Mapping(source = "id", target = "id", qualifiedByName = "longToString")
  ProductCreateResultVO toCreateResultVO(CreateProductResponse response);

  @Named("longToString")
  default String longToString(Long value) {
    return value == null ? null : String.valueOf(value);
  }
}
