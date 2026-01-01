package com.tenghe.corebackend.kronos.infrastructure.adapter;

import com.tenghe.corebackend.kronos.infrastructure.feign.GatewayFeignClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.GatewayServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.CreateGatewayRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.GatewayListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.UpdateGatewayRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 网关服务适配器。
 * 封装对 device-service 网关接口的调用。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayServiceAdapter implements GatewayServiceClient {

  private final GatewayFeignClient feignClient;

  @Override
  public DeviceServiceApiResponse<DeviceServicePageResponse<GatewayListItem>> listGateways(
      String keyword, Integer page, Integer size) {
    log.info("CALL device-service listGateways keyword={} page={} size={}", keyword, page, size);
    long start = System.currentTimeMillis();
    var response = feignClient.listGateways(keyword, page, size);
    log.info("CALL device-service listGateways costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<GatewayListItem> createGateway(CreateGatewayRequest request) {
    log.info("CALL device-service createGateway name={} sn={}", request.getName(), request.getSn());
    long start = System.currentTimeMillis();
    var response = feignClient.createGateway(request);
    log.info("CALL device-service createGateway costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> updateGateway(Long gatewayId, UpdateGatewayRequest request) {
    log.info("CALL device-service updateGateway gatewayId={}", gatewayId);
    long start = System.currentTimeMillis();
    var response = feignClient.updateGateway(gatewayId, request);
    log.info("CALL device-service updateGateway costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> deleteGateway(Long gatewayId) {
    log.info("CALL device-service deleteGateway gatewayId={}", gatewayId);
    long start = System.currentTimeMillis();
    var response = feignClient.deleteGateway(gatewayId);
    log.info("CALL device-service deleteGateway costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> enableGateway(Long gatewayId) {
    log.info("CALL device-service enableGateway gatewayId={}", gatewayId);
    long start = System.currentTimeMillis();
    var response = feignClient.enableGateway(gatewayId);
    log.info("CALL device-service enableGateway costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }

  @Override
  public DeviceServiceApiResponse<Void> disableGateway(Long gatewayId) {
    log.info("CALL device-service disableGateway gatewayId={}", gatewayId);
    long start = System.currentTimeMillis();
    var response = feignClient.disableGateway(gatewayId);
    log.info("CALL device-service disableGateway costMs={} success={}",
        System.currentTimeMillis() - start, response.isOk());
    return response;
  }
}
