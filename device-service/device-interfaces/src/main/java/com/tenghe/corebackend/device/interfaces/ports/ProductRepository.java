package com.tenghe.corebackend.device.interfaces.ports;

import com.tenghe.corebackend.device.interfaces.portdata.ProductPortData;

import java.util.List;

public interface ProductRepository {
  ProductPortData save(ProductPortData product);

  ProductPortData update(ProductPortData product);

  ProductPortData findById(Long id);

  ProductPortData findByProductKey(String productKey);

  List<ProductPortData> listAll();

  List<ProductPortData> listByDeviceModelId(Long deviceModelId);
}
