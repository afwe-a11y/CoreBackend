package com.tenghe.corebackend.device.api.v1;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.product.CreateProductRequest;
import com.tenghe.corebackend.device.api.dto.product.ProductDetailResponse;
import com.tenghe.corebackend.device.api.dto.product.ProductListItem;
import com.tenghe.corebackend.device.api.dto.product.UpdateProductRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "产品")
public interface ProductApi {

  @Operation(summary = "查询产品分页")
  @GetMapping
  ApiResponse<PageResponse<ProductListItem>> listProducts(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "keyword", required = false) String keyword);

  @Operation(summary = "查询产品详情")
  @GetMapping("/{productId}")
  ApiResponse<ProductDetailResponse> getProduct(@PathVariable("productId") Long productId);

  @Operation(summary = "创建产品")
  @PostMapping
  ApiResponse<Long> createProduct(@RequestBody CreateProductRequest request);

  @Operation(summary = "更新产品")
  @PutMapping("/{productId}")
  ApiResponse<Void> updateProduct(
      @PathVariable("productId") Long productId,
      @RequestBody UpdateProductRequest request);

  @Operation(summary = "删除产品")
  @DeleteMapping("/{productId}")
  ApiResponse<Void> deleteProduct(@PathVariable("productId") Long productId);
}
