package com.tenghe.corebackend.kronos.infrastructure.feign;

import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServiceApiResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.CreateGatewayRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.GatewayListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.gateway.UpdateGatewayRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 网关 Feign 客户端。
 * 调用 device-service 的网关接口。
 */
@FeignClient(name = "device-service", contextId = "gatewayClient", path = "/api/gateways")
public interface GatewayFeignClient {

  @GetMapping
  DeviceServiceApiResponse<DeviceServicePageResponse<GatewayListItem>> listGateways(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size);

  @PostMapping
  DeviceServiceApiResponse<GatewayListItem> createGateway(@RequestBody CreateGatewayRequest request);

  @PutMapping("/{gatewayId}")
  DeviceServiceApiResponse<Void> updateGateway(
      @PathVariable("gatewayId") Long gatewayId,
      @RequestBody UpdateGatewayRequest request);

  @DeleteMapping("/{gatewayId}")
  DeviceServiceApiResponse<Void> deleteGateway(@PathVariable("gatewayId") Long gatewayId);

  @PostMapping("/{gatewayId}/enable")
  DeviceServiceApiResponse<Void> enableGateway(@PathVariable("gatewayId") Long gatewayId);

  @PostMapping("/{gatewayId}/disable")
  DeviceServiceApiResponse<Void> disableGateway(@PathVariable("gatewayId") Long gatewayId);
}
