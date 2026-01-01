package com.tenghe.corebackend.kronos.interfaces.downstream;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.ProductListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductMappingRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductRequest;

/**
 * Product Service 下游服务客户端端口。
 * 定义对 Foundation device-service 产品接口的调用。
 */
public interface ProductServiceClient {

  /**
   * 分页查询产品列表
   */
  DeviceServiceApiResponse<DeviceServicePageResponse<ProductListItem>> listProducts(
      Integer page, Integer size);

  /**
   * 创建产品
   */
  DeviceServiceApiResponse<CreateProductResponse> createProduct(CreateProductRequest request);

  /**
   * 更新产品
   */
  DeviceServiceApiResponse<Void> updateProduct(Long productId, UpdateProductRequest request);

  /**
   * 更新产品协议映射
   */
  DeviceServiceApiResponse<Void> updateProductMapping(Long productId, UpdateProductMappingRequest request);

  /**
   * 删除产品
   */
  DeviceServiceApiResponse<Void> deleteProduct(Long productId);
}
