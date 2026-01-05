package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.interfaces.portdata.GatewayPortData;

import java.util.List;

public interface GatewayRepositoryPort {
  GatewayPortData save(GatewayPortData gateway);

  GatewayPortData update(GatewayPortData gateway);

  GatewayPortData findById(Long id);

  GatewayPortData findBySn(String sn);

  List<GatewayPortData> listAll();

  List<GatewayPortData> searchByNameOrSn(String keyword);
}
