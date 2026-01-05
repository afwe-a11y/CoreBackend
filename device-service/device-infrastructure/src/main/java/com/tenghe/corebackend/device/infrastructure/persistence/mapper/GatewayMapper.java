package com.tenghe.corebackend.device.infrastructure.persistence.mapper;

import com.tenghe.corebackend.device.infrastructure.persistence.po.GatewayPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GatewayMapper {
  int insert(GatewayPo gateway);

  int update(GatewayPo gateway);

  GatewayPo findById(@Param("id") Long id);

  GatewayPo findBySn(@Param("sn") String sn);

  List<GatewayPo> listAll();

  List<GatewayPo> searchByNameOrSn(@Param("keyword") String keyword);
}
