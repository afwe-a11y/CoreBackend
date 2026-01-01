package com.tenghe.corebackend.kronos.controller.web;

import com.tenghe.corebackend.kronos.api.common.ApiResponse;
import com.tenghe.corebackend.kronos.api.common.PageResponse;
import com.tenghe.corebackend.kronos.api.dto.product.ProductCreateDTO;
import com.tenghe.corebackend.kronos.api.dto.product.ProductMappingUpdateDTO;
import com.tenghe.corebackend.kronos.api.dto.product.ProductUpdateDTO;
import com.tenghe.corebackend.kronos.api.vo.product.ProductCreateResultVO;
import com.tenghe.corebackend.kronos.api.vo.product.ProductListVO;
import com.tenghe.corebackend.kronos.application.ProductApplicationService;
import com.tenghe.corebackend.kronos.controller.converter.ProductVOConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品控制器（BFF 层）。
 * 对接前端，编排调用 device-service。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductApplicationService productService;
  private final ProductVOConverter converter = ProductVOConverter.INSTANCE;

  /**
   * 分页查询产品列表
   */
  @GetMapping
  public ApiResponse<PageResponse<ProductListVO>> list(
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    log.info("REQ GET /api/v1/products page={} size={}", page, size);
    long start = System.currentTimeMillis();

    var result = productService.listProducts(page, size);
    var vos = result.getItems().stream().map(converter::toListVO).toList();
    var pageResponse = PageResponse.of(vos, result.getTotal(), result.getPage(), result.getSize());

    log.info("RES GET /api/v1/products total={} costMs={}", result.getTotal(), System.currentTimeMillis() - start);
    return ApiResponse.success(pageResponse);
  }

  /**
   * 创建产品
   */
  @PostMapping
  public ApiResponse<ProductCreateResultVO> create(@Valid @RequestBody ProductCreateDTO dto) {
    log.info("REQ POST /api/v1/products name={}", dto.getName());
    long start = System.currentTimeMillis();

    var result = productService.createProduct(
        dto.getProductType(), dto.getName(), dto.getDeviceModelId(),
        dto.getAccessMode(), dto.getDescription(), dto.getProtocolMappings());
    var vo = converter.toCreateResultVO(result);

    log.info("RES POST /api/v1/products id={} costMs={}", result.getId(), System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 更新产品
   */
  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto) {
    log.info("REQ PUT /api/v1/products/{}", id);
    long start = System.currentTimeMillis();

    productService.updateProduct(id, dto.getName(), dto.getDescription());

    log.info("RES PUT /api/v1/products/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 更新产品协议映射
   */
  @PutMapping("/{id}/protocol-mappings")
  public ApiResponse<Void> updateMapping(@PathVariable Long id, @Valid @RequestBody ProductMappingUpdateDTO dto) {
    log.info("REQ PUT /api/v1/products/{}/protocol-mappings", id);
    long start = System.currentTimeMillis();

    productService.updateProductMapping(id, dto.getMappings());

    log.info("RES PUT /api/v1/products/{}/protocol-mappings costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 删除产品
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    log.info("REQ DELETE /api/v1/products/{}", id);
    long start = System.currentTimeMillis();

    productService.deleteProduct(id);

    log.info("RES DELETE /api/v1/products/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }
}
