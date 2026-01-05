package com.tenghe.corebackend.device.controller.web;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.product.*;
import com.tenghe.corebackend.device.application.command.CreateProductCommand;
import com.tenghe.corebackend.device.application.command.UpdateProductCommand;
import com.tenghe.corebackend.device.application.command.UpdateProductMappingCommand;
import com.tenghe.corebackend.device.application.service.ProductApplicationService;
import com.tenghe.corebackend.device.application.service.result.CreateProductResult;
import com.tenghe.corebackend.device.application.service.result.PageResult;
import com.tenghe.corebackend.device.application.service.result.ProductListItemResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final ProductApplicationService productService;

  public ProductController(ProductApplicationService productService) {
    this.productService = productService;
  }

  @GetMapping
  public ApiResponse<PageResponse<ProductListItem>> listProducts(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    PageResult<ProductListItemResult> result = productService.listProducts(page, size);
    List<ProductListItem> items = result.getItems().stream()
        .map(this::toListItem)
        .collect(Collectors.toList());
    return ApiResponse.ok(new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize()));
  }

  @PostMapping
  public ApiResponse<CreateProductResponse> createProduct(@RequestBody CreateProductRequest request) {
    CreateProductCommand command = new CreateProductCommand();
    command.setProductType(request.getProductType());
    command.setName(request.getName());
    command.setDeviceModelId(request.getDeviceModelId());
    command.setAccessMode(request.getAccessMode());
    command.setDescription(request.getDescription());
    command.setProtocolMapping(request.getProtocolMapping());
    CreateProductResult result = productService.createProduct(command);
    return ApiResponse.ok(new CreateProductResponse(result.getId(), result.getProductKey(), result.getProductSecret()));
  }

  @PutMapping("/{productId}")
  public ApiResponse<Void> updateProduct(
      @PathVariable("productId") Long productId,
      @RequestBody UpdateProductRequest request) {
    UpdateProductCommand command = new UpdateProductCommand();
    command.setProductId(productId);
    command.setName(request.getName());
    command.setDescription(request.getDescription());
    productService.updateProduct(command);
    return ApiResponse.ok(null);
  }

  @PutMapping("/{productId}/mapping")
  public ApiResponse<Void> updateMapping(
      @PathVariable("productId") Long productId,
      @RequestBody UpdateProductMappingRequest request) {
    UpdateProductMappingCommand command = new UpdateProductMappingCommand();
    command.setProductId(productId);
    command.setProtocolMapping(request.getProtocolMapping());
    productService.updateProtocolMapping(command);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{productId}")
  public ApiResponse<Void> deleteProduct(@PathVariable("productId") Long productId) {
    productService.deleteProduct(productId);
    return ApiResponse.ok(null);
  }

  private ProductListItem toListItem(ProductListItemResult result) {
    ProductListItem item = new ProductListItem();
    item.setId(result.getId());
    item.setProductType(result.getProductType());
    item.setName(result.getName());
    item.setProductKey(result.getProductKey());
    item.setDeviceModelId(result.getDeviceModelId());
    item.setAccessMode(result.getAccessMode());
    item.setDescription(result.getDescription());
    return item;
  }
}
