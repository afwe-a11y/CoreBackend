package com.tenghe.corebackend.kronos.interfaces.downstream;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.CreateGatewayRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.GatewayListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.UpdateGatewayRequest;

/**
 * Gateway Service 下游服务客户端端口。
 * 定义对 Foundation device-service 网关接口的调用。
 */
public interface GatewayServiceClient {

  /**
   * 分页查询网关列表
   */
  DeviceServiceApiResponse<DeviceServicePageResponse<GatewayListItem>> listGateways(
      String keyword, Integer page, Integer size);

  /**
   * 创建网关
   */
  DeviceServiceApiResponse<GatewayListItem> createGateway(CreateGatewayRequest request);

  /**
   * 更新网关
   */
  DeviceServiceApiResponse<Void> updateGateway(Long gatewayId, UpdateGatewayRequest request);

  /**
   * 删除网关
   */
  DeviceServiceApiResponse<Void> deleteGateway(Long gatewayId);

  /**
   * 启用网关
   */
  DeviceServiceApiResponse<Void> enableGateway(Long gatewayId);

  /**
   * 禁用网关
   */
  DeviceServiceApiResponse<Void> disableGateway(Long gatewayId);
}
