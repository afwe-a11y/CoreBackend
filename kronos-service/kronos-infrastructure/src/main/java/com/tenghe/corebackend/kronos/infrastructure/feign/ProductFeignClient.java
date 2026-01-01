package com.tenghe.corebackend.kronos.infrastructure.feign;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.ProductListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductMappingRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 产品 Feign 客户端。
 * 调用 device-service 的产品接口。
 */
@FeignClient(name = "device-service", contextId = "productClient", path = "/api/products")
public interface ProductFeignClient {

  @GetMapping
  DeviceServiceApiResponse<DeviceServicePageResponse<ProductListItem>> listProducts(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size);

  @PostMapping
  DeviceServiceApiResponse<CreateProductResponse> createProduct(@RequestBody CreateProductRequest request);

  @PutMapping("/{productId}")
  DeviceServiceApiResponse<Void> updateProduct(
      @PathVariable("productId") Long productId,
      @RequestBody UpdateProductRequest request);

  @PutMapping("/{productId}/mapping")
  DeviceServiceApiResponse<Void> updateProductMapping(
      @PathVariable("productId") Long productId,
      @RequestBody UpdateProductMappingRequest request);

  @DeleteMapping("/{productId}")
  DeviceServiceApiResponse<Void> deleteProduct(@PathVariable("productId") Long productId);
}
