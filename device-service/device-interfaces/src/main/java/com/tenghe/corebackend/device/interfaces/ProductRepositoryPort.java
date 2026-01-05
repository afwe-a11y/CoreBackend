package com.tenghe.corebackend.device.interfaces;

import com.tenghe.corebackend.device.model.Product;

import java.util.List;

public interface ProductRepositoryPort {
  Product save(Product product);

  Product update(Product product);

  Product findById(Long id);

  Product findByProductKey(String productKey);

  List<Product> listAll();

  List<Product> listByDeviceModelId(Long deviceModelId);
}
