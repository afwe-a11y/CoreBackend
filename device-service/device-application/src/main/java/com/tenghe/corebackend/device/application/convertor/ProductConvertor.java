package com.tenghe.corebackend.device.application.convertor;

import com.tenghe.corebackend.device.api.dto.product.ProductListItem;
import com.tenghe.corebackend.device.interfaces.portdata.ProductPortData;

public class ProductConvertor {

  public static ProductListItem toListItem(ProductPortData product) {
    if (product == null) {
      return null;
    }
    ProductListItem item = new ProductListItem();
    item.setId(product.getId());
    item.setName(product.getName());
    item.setDescription(product.getDescription());
    item.setProductType(product.getProductType() == null ? null : product.getProductType().name());
    item.setDeviceModelId(product.getDeviceModelId());
    return item;
  }
}
