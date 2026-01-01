package com.tenghe.corebackend.kronos.application;

import com.tenghe.corebackend.kronos.application.exception.BusinessException;
import com.tenghe.corebackend.kronos.application.exception.ErrorCode;
import com.tenghe.corebackend.kronos.interfaces.downstream.GatewayServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.CreateGatewayRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.GatewayListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.UpdateGatewayRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 网关应用服务（BFF 编排层）。
 * 负责调用 device-service 并进行数据编排。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayApplicationService {

  private final GatewayServiceClient gatewayClient;

  /**
   * 分页查询网关列表
   *
   * @param keyword 关键词
   * @param page    页码
   * @param size    每页数量
   * @return 分页结果
   */
  public DeviceServicePageResponse<GatewayListItem> listGateways(String keyword, Integer page, Integer size) {
    log.info("查询网关列表: keyword={}, page={}, size={}", keyword, page, size);
    var response = gatewayClient.listGateways(keyword, page, size);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    return response.getData();
  }

  /**
   * 创建网关
   *
   * @param name      名称
   * @param type      类型
   * @param sn        序列号
   * @param productId 产品ID
   * @param stationId 电站ID
   * @return 创建结果
   */
  public GatewayListItem createGateway(String name, String type, String sn, Long productId, Long stationId) {
    log.info("创建网关: name={}, type={}, sn={}", name, type, sn);
    var request = CreateGatewayRequest.builder()
        .name(name)
        .type(type)
        .sn(sn)
        .productId(productId)
        .stationId(stationId)
        .build();
    var response = gatewayClient.createGateway(request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("网关创建成功: id={}", response.getData().getId());
    return response.getData();
  }

  /**
   * 更新网关
   *
   * @param gatewayId 网关ID
   * @param name      名称
   * @param sn        序列号
   * @param productId 产品ID
   * @param stationId 电站ID
   */
  public void updateGateway(Long gatewayId, String name, String sn, Long productId, Long stationId) {
    log.info("更新网关: gatewayId={}", gatewayId);
    var request = UpdateGatewayRequest.builder()
        .name(name)
        .sn(sn)
        .productId(productId)
        .stationId(stationId)
        .build();
    var response = gatewayClient.updateGateway(gatewayId, request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("网关更新成功: gatewayId={}", gatewayId);
  }

  /**
   * 删除网关
   *
   * @param gatewayId 网关ID
   */
  public void deleteGateway(Long gatewayId) {
    log.info("删除网关: gatewayId={}", gatewayId);
    var response = gatewayClient.deleteGateway(gatewayId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("网关删除成功: gatewayId={}", gatewayId);
  }

  /**
   * 启用网关
   *
   * @param gatewayId 网关ID
   */
  public void enableGateway(Long gatewayId) {
    log.info("启用网关: gatewayId={}", gatewayId);
    var response = gatewayClient.enableGateway(gatewayId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("网关启用成功: gatewayId={}", gatewayId);
  }

  /**
   * 禁用网关
   *
   * @param gatewayId 网关ID
   */
  public void disableGateway(Long gatewayId) {
    log.info("禁用网关: gatewayId={}", gatewayId);
    var response = gatewayClient.disableGateway(gatewayId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("网关禁用成功: gatewayId={}", gatewayId);
  }
}
