package com.tenghe.corebackend.kronos.application;

import com.tenghe.corebackend.kronos.application.exception.BusinessException;
import com.tenghe.corebackend.kronos.application.exception.ErrorCode;
import com.tenghe.corebackend.kronos.interfaces.downstream.ProductServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.CreateProductResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.ProductListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductMappingRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.product.UpdateProductRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 产品应用服务（BFF 编排层）。
 * 负责调用 device-service 并进行数据编排。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductApplicationService {

  private final ProductServiceClient productClient;

  /**
   * 分页查询产品列表
   *
   * @param page 页码
   * @param size 每页数量
   * @return 分页结果
   */
  public DeviceServicePageResponse<ProductListItem> listProducts(Integer page, Integer size) {
    log.info("查询产品列表: page={}, size={}", page, size);
    var response = productClient.listProducts(page, size);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    return response.getData();
  }

  /**
   * 创建产品
   *
   * @param productType   产品类型
   * @param name          名称
   * @param deviceModelId 设备模型ID
   * @param accessMode    接入模式
   * @param description   描述
   * @param protocolMappings 协议映射
   * @return 创建结果
   */
  public CreateProductResponse createProduct(String productType, String name, Long deviceModelId,
      String accessMode, String description, Map<String, String> protocolMappings) {
    log.info("创建产品: name={}, deviceModelId={}", name, deviceModelId);
    var request = CreateProductRequest.builder()
        .productType(productType)
        .name(name)
        .deviceModelId(deviceModelId)
        .accessMode(accessMode)
        .description(description)
        .protocolMapping(protocolMappings)
        .build();
    var response = productClient.createProduct(request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("产品创建成功: id={}, productKey={}", response.getData().getId(), response.getData().getProductKey());
    return response.getData();
  }

  /**
   * 更新产品
   *
   * @param productId   产品ID
   * @param name        名称
   * @param description 描述
   */
  public void updateProduct(Long productId, String name, String description) {
    log.info("更新产品: productId={}", productId);
    var request = UpdateProductRequest.builder()
        .name(name)
        .description(description)
        .build();
    var response = productClient.updateProduct(productId, request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("产品更新成功: productId={}", productId);
  }

  /**
   * 更新产品协议映射
   *
   * @param productId        产品ID
   * @param protocolMappings 协议映射
   */
  public void updateProductMapping(Long productId, Map<String, String> protocolMappings) {
    log.info("更新产品协议映射: productId={}", productId);
    var request = UpdateProductMappingRequest.builder()
        .protocolMapping(protocolMappings)
        .build();
    var response = productClient.updateProductMapping(productId, request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("产品协议映射更新成功: productId={}", productId);
  }

  /**
   * 删除产品
   *
   * @param productId 产品ID
   */
  public void deleteProduct(Long productId) {
    log.info("删除产品: productId={}", productId);
    var response = productClient.deleteProduct(productId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("产品删除成功: productId={}", productId);
  }
}
