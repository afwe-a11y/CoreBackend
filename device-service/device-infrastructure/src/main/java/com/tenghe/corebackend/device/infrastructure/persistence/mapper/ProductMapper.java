package com.tenghe.corebackend.device.infrastructure.persistence.mapper;

import com.tenghe.corebackend.device.infrastructure.persistence.po.ProductPo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {
    int insert(ProductPo product);

    int update(ProductPo product);

    ProductPo findById(@Param("id") Long id);

    ProductPo findByProductKey(@Param("productKey") String productKey);

    List<ProductPo> listAll();

    List<ProductPo> listByDeviceModelId(@Param("deviceModelId") Long deviceModelId);
}
