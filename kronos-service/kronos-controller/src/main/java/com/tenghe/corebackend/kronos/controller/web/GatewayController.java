package com.tenghe.corebackend.kronos.controller.web;

import com.tenghe.corebackend.kronos.api.common.ApiResponse;
import com.tenghe.corebackend.kronos.api.common.PageResponse;
import com.tenghe.corebackend.kronos.api.dto.gateway.GatewayCreateDTO;
import com.tenghe.corebackend.kronos.api.dto.gateway.GatewayUpdateDTO;
import com.tenghe.corebackend.kronos.api.vo.gateway.GatewayListVO;
import com.tenghe.corebackend.kronos.application.GatewayApplicationService;
import com.tenghe.corebackend.kronos.controller.converter.GatewayVOConverter;
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
 * 网关控制器（BFF 层）。
 * 对接前端，编排调用 device-service。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/gateways")
@RequiredArgsConstructor
public class GatewayController {

  private final GatewayApplicationService gatewayService;
  private final GatewayVOConverter converter = GatewayVOConverter.INSTANCE;

  /**
   * 分页查询网关列表
   */
  @GetMapping
  public ApiResponse<PageResponse<GatewayListVO>> list(
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    log.info("REQ GET /api/v1/gateways keyword={} page={} size={}", keyword, page, size);
    long start = System.currentTimeMillis();

    var result = gatewayService.listGateways(keyword, page, size);
    var vos = result.getItems().stream().map(converter::toListVO).toList();
    var pageResponse = PageResponse.of(vos, result.getTotal(), result.getPage(), result.getSize());

    log.info("RES GET /api/v1/gateways total={} costMs={}", result.getTotal(), System.currentTimeMillis() - start);
    return ApiResponse.success(pageResponse);
  }

  /**
   * 创建网关
   */
  @PostMapping
  public ApiResponse<GatewayListVO> create(@Valid @RequestBody GatewayCreateDTO dto) {
    log.info("REQ POST /api/v1/gateways name={} sn={}", dto.getName(), dto.getSn());
    long start = System.currentTimeMillis();

    var result = gatewayService.createGateway(
        dto.getName(), dto.getType(), dto.getSn(), dto.getProductId(), dto.getStationId());
    var vo = converter.toListVO(result);

    log.info("RES POST /api/v1/gateways id={} costMs={}", result.getId(), System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 更新网关
   */
  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody GatewayUpdateDTO dto) {
    log.info("REQ PUT /api/v1/gateways/{}", id);
    long start = System.currentTimeMillis();

    gatewayService.updateGateway(id, dto.getName(), dto.getSn(), dto.getProductId(), dto.getStationId());

    log.info("RES PUT /api/v1/gateways/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 删除网关
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    log.info("REQ DELETE /api/v1/gateways/{}", id);
    long start = System.currentTimeMillis();

    gatewayService.deleteGateway(id);

    log.info("RES DELETE /api/v1/gateways/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 启用网关
   */
  @PostMapping("/{id}/enable")
  public ApiResponse<Void> enable(@PathVariable Long id) {
    log.info("REQ POST /api/v1/gateways/{}/enable", id);
    long start = System.currentTimeMillis();

    gatewayService.enableGateway(id);

    log.info("RES POST /api/v1/gateways/{}/enable costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 禁用网关
   */
  @PostMapping("/{id}/disable")
  public ApiResponse<Void> disable(@PathVariable Long id) {
    log.info("REQ POST /api/v1/gateways/{}/disable", id);
    long start = System.currentTimeMillis();

    gatewayService.disableGateway(id);

    log.info("RES POST /api/v1/gateways/{}/disable costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }
}
