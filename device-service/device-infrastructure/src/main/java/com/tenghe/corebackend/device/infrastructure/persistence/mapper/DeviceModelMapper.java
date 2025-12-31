package com.tenghe.corebackend.device.infrastructure.persistence.mapper;

import com.tenghe.corebackend.device.infrastructure.persistence.po.DeviceModelPo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface DeviceModelMapper {
    int insert(DeviceModelPo model);

    int update(DeviceModelPo model);

    DeviceModelPo findById(@Param("id") Long id);

    DeviceModelPo findByIdentifier(@Param("identifier") String identifier);

    List<DeviceModelPo> listAll();

    List<DeviceModelPo> listByParentId(@Param("parentModelId") Long parentModelId);
}
