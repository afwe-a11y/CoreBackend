package com.tenghe.corebackend.device.infrastructure.persistence.mapper;

import com.tenghe.corebackend.device.infrastructure.persistence.po.DeviceModelPointPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeviceModelPointMapper {
  int insert(DeviceModelPointPo point);

  int update(DeviceModelPointPo point);

  DeviceModelPointPo findByModelIdAndIdentifier(
      @Param("modelId") Long modelId,
      @Param("identifier") String identifier);

  List<DeviceModelPointPo> listByModelId(@Param("modelId") Long modelId);
}
