package com.tenghe.corebackend.device.infrastructure.persistence.mapper;

import com.tenghe.corebackend.device.infrastructure.persistence.po.DevicePo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceMapper {
    int insert(DevicePo device);

    int update(DevicePo device);

    DevicePo findById(@Param("id") Long id);

    DevicePo findByDeviceKey(@Param("deviceKey") String deviceKey);

    List<DevicePo> listAll();

    List<DevicePo> listByProductId(@Param("productId") Long productId);

    List<DevicePo> listByGatewayId(@Param("gatewayId") Long gatewayId);

    List<DevicePo> search(
            @Param("keyword") String keyword,
            @Param("productId") Long productId,
            @Param("idKeyword") Long idKeyword);

    long countByProductId(@Param("productId") Long productId);
}
