package com.tenghe.corebackend.kronos.infrastructure.adapter;

import com.tenghe.corebackend.kronos.infrastructure.feign.ProductFeignClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.ProductServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.ProductListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductMappingRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 产品服务适配器。
 * 封装对 device-service 产品接口的调用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceAdapter implements ProductServiceClient {

  private final ProductFeignClient feignClient;

  @Override
  public DeviceServiceApiResponse<DeviceServicePageResponse<ProductListItem>> listProducts(
      Integer page, Integer size) {
    log.info("CALL device-service listProducts page={} size={}", page, size);
    long start = System.currentTimeMillis();
    var response = feignClient.listProducts(page, size);
    log.info("CALL device-service listProducts costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<CreateProductResponse> createProduct(CreateProductRequest request) {
    log.info("CALL device-service createProduct name={}", request.getName());
    long start = System.currentTimeMillis();
    var response = feignClient.createProduct(request);
    log.info("CALL device-service createProduct costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> updateProduct(Long productId, UpdateProductRequest request) {
    log.info("CALL device-service updateProduct productId={}", productId);
    long start = System.currentTimeMillis();
    var response = feignClient.updateProduct(productId, request);
    log.info("CALL device-service updateProduct costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> updateProductMapping(Long productId, UpdateProductMappingRequest request) {
    log.info("CALL device-service updateProductMapping productId={}", productId);
    long start = System.currentTimeMillis();
    var response = feignClient.updateProductMapping(productId, request);
    log.info("CALL device-service updateProductMapping costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> deleteProduct(Long productId) {
    log.info("CALL device-service deleteProduct productId={}", productId);
    long start = System.currentTimeMillis();
    var response = feignClient.deleteProduct(productId);
    log.info("CALL device-service deleteProduct costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }
}
