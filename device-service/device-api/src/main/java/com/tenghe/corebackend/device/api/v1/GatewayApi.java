package com.tenghe.corebackend.device.api.v1;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.gateway.CreateGatewayRequest;
import com.tenghe.corebackend.device.api.dto.gateway.GatewayListItem;
import com.tenghe.corebackend.device.api.dto.gateway.UpdateGatewayRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "网关")
public interface GatewayApi {

  @Operation(summary = "查询网关分页")
  @GetMapping
  ApiResponse<PageResponse<GatewayListItem>> listGateways(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "keyword", required = false) String keyword);

  @Operation(summary = "创建网关")
  @PostMapping
  ApiResponse<GatewayListItem> createGateway(@RequestBody CreateGatewayRequest request);

  @Operation(summary = "更新网关")
  @PutMapping("/{gatewayId}")
  ApiResponse<Void> updateGateway(
      @PathVariable("gatewayId") Long gatewayId,
      @RequestBody UpdateGatewayRequest request);

  @Operation(summary = "删除网关")
  @DeleteMapping("/{gatewayId}")
  ApiResponse<Void> deleteGateway(@PathVariable("gatewayId") Long gatewayId);
}
