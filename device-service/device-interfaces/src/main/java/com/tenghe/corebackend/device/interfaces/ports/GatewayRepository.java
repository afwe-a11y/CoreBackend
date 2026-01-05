package com.tenghe.corebackend.device.interfaces.ports;

import com.tenghe.corebackend.device.interfaces.portdata.GatewayPortData;

import java.util.List;

public interface GatewayRepository {
  GatewayPortData save(GatewayPortData gateway);

  GatewayPortData update(GatewayPortData gateway);

  GatewayPortData findById(Long id);

  GatewayPortData findBySn(String sn);

  List<GatewayPortData> listAll();

  List<GatewayPortData> searchByNameOrSn(String keyword);
}
