package com.tenghe.corebackend.device.controller.web;

import com.tenghe.corebackend.device.api.dto.common.ApiResponse;
import com.tenghe.corebackend.device.api.dto.common.PageResponse;
import com.tenghe.corebackend.device.api.dto.gateway.CreateGatewayRequest;
import com.tenghe.corebackend.device.api.dto.gateway.GatewayListItem;
import com.tenghe.corebackend.device.api.dto.gateway.UpdateGatewayRequest;
import com.tenghe.corebackend.device.application.command.CreateGatewayCommand;
import com.tenghe.corebackend.device.application.command.ToggleGatewayCommand;
import com.tenghe.corebackend.device.application.command.UpdateGatewayCommand;
import com.tenghe.corebackend.device.application.service.GatewayApplicationService;
import com.tenghe.corebackend.device.application.service.result.GatewayListItemResult;
import com.tenghe.corebackend.device.application.service.result.PageResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/gateways")
public class GatewayController {
  private final GatewayApplicationService gatewayService;

  public GatewayController(GatewayApplicationService gatewayService) {
    this.gatewayService = gatewayService;
  }

  @GetMapping
  public ApiResponse<PageResponse<GatewayListItem>> listGateways(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    PageResult<GatewayListItemResult> result = gatewayService.listGateways(keyword, page, size);
    List<GatewayListItem> items = result.getItems().stream()
        .map(this::toListItem)
        .collect(Collectors.toList());
    return ApiResponse.ok(new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize()));
  }

  @PostMapping
  public ApiResponse<GatewayListItem> createGateway(@RequestBody CreateGatewayRequest request) {
    CreateGatewayCommand command = new CreateGatewayCommand();
    command.setName(request.getName());
    command.setType(request.getType());
    command.setSn(request.getSn());
    command.setProductId(request.getProductId());
    command.setStationId(request.getStationId());
    GatewayListItemResult result = gatewayService.createGateway(command);
    return ApiResponse.ok(toListItem(result));
  }

  @PutMapping("/{gatewayId}")
  public ApiResponse<Void> updateGateway(
      @PathVariable("gatewayId") Long gatewayId,
      @RequestBody UpdateGatewayRequest request) {
    UpdateGatewayCommand command = new UpdateGatewayCommand();
    command.setGatewayId(gatewayId);
    command.setName(request.getName());
    command.setSn(request.getSn());
    command.setProductId(request.getProductId());
    command.setStationId(request.getStationId());
    gatewayService.updateGateway(command);
    return ApiResponse.ok(null);
  }

  @DeleteMapping("/{gatewayId}")
  public ApiResponse<Void> deleteGateway(@PathVariable("gatewayId") Long gatewayId) {
    gatewayService.deleteGateway(gatewayId);
    return ApiResponse.ok(null);
  }

  @PostMapping("/{gatewayId}/enable")
  public ApiResponse<Void> enableGateway(@PathVariable("gatewayId") Long gatewayId) {
    ToggleGatewayCommand command = new ToggleGatewayCommand();
    command.setGatewayId(gatewayId);
    command.setEnabled(true);
    gatewayService.toggleGateway(command);
    return ApiResponse.ok(null);
  }

  @PostMapping("/{gatewayId}/disable")
  public ApiResponse<Void> disableGateway(@PathVariable("gatewayId") Long gatewayId) {
    ToggleGatewayCommand command = new ToggleGatewayCommand();
    command.setGatewayId(gatewayId);
    command.setEnabled(false);
    gatewayService.toggleGateway(command);
    return ApiResponse.ok(null);
  }

  private GatewayListItem toListItem(GatewayListItemResult result) {
    GatewayListItem item = new GatewayListItem();
    item.setId(result.getId());
    item.setName(result.getName());
    item.setType(result.getType());
    item.setSn(result.getSn());
    item.setProductId(result.getProductId());
    item.setStationId(result.getStationId());
    item.setStatus(result.getStatus());
    item.setEnabled(result.isEnabled());
    return item;
  }
}
