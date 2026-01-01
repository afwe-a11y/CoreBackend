package com.tenghe.corebackend.kronos.controller.web;

import com.tenghe.corebackend.kronos.api.common.ApiResponse;
import com.tenghe.corebackend.kronos.api.common.PageResponse;
import com.tenghe.corebackend.kronos.api.dto.device.DeviceCreateDTO;
import com.tenghe.corebackend.kronos.api.dto.device.DeviceImportDTO;
import com.tenghe.corebackend.kronos.api.dto.device.DeviceUpdateDTO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceCreateResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceExportResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceImportCommitResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceImportPreviewResultVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceListVO;
import com.tenghe.corebackend.kronos.api.vo.device.DeviceTelemetryVO;
import com.tenghe.corebackend.kronos.application.DeviceApplicationService;
import com.tenghe.corebackend.kronos.controller.converter.DeviceVOConverter;
import jakarta.validation.Valid;
import java.util.List;
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
 * 设备控制器（BFF 层）。
 * 对接前端，编排调用 device-service。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final DeviceApplicationService deviceService;
  private final DeviceVOConverter converter = DeviceVOConverter.INSTANCE;

  /**
   * 分页查询设备列表
   */
  @GetMapping
  public ApiResponse<PageResponse<DeviceListVO>> list(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    log.info("REQ GET /api/v1/devices productId={} keyword={} page={} size={}", productId, keyword, page, size);
    long start = System.currentTimeMillis();

    var result = deviceService.listDevices(productId, keyword, page, size);
    var vos = result.getItems().stream().map(converter::toListVO).toList();
    var pageResponse = PageResponse.of(vos, result.getTotal(), result.getPage(), result.getSize());

    log.info("RES GET /api/v1/devices total={} costMs={}", result.getTotal(), System.currentTimeMillis() - start);
    return ApiResponse.success(pageResponse);
  }

  /**
   * 创建设备
   */
  @PostMapping
  public ApiResponse<DeviceCreateResultVO> create(@Valid @RequestBody DeviceCreateDTO dto) {
    log.info("REQ POST /api/v1/devices name={} productId={}", dto.getName(), dto.getProductId());
    long start = System.currentTimeMillis();

    var result = deviceService.createDevice(
        dto.getName(), dto.getProductId(), dto.getDeviceKey(), dto.getGatewayId(), dto.getDynamicAttributes());
    var vo = converter.toCreateResultVO(result);

    log.info("RES POST /api/v1/devices id={} costMs={}", result.getId(), System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 更新设备
   */
  @PutMapping("/{id}")
  public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody DeviceUpdateDTO dto) {
    log.info("REQ PUT /api/v1/devices/{}", id);
    long start = System.currentTimeMillis();

    deviceService.updateDevice(id, dto.getName(), dto.getGatewayId(), dto.getDynamicAttributes());

    log.info("RES PUT /api/v1/devices/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 删除设备
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    log.info("REQ DELETE /api/v1/devices/{}", id);
    long start = System.currentTimeMillis();

    deviceService.deleteDevice(id);

    log.info("RES DELETE /api/v1/devices/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 设备导入预览
   */
  @PostMapping("/import/preview")
  public ApiResponse<DeviceImportPreviewResultVO> importPreview(@Valid @RequestBody List<DeviceImportDTO> dtos) {
    log.info("REQ POST /api/v1/devices/import/preview count={}", dtos.size());
    long start = System.currentTimeMillis();

    var rows = converter.toImportRows(dtos);
    var result = deviceService.importPreview(rows);
    var vo = converter.toImportPreviewResultVO(result);

    log.info("RES POST /api/v1/devices/import/preview total={} costMs={}", result.getTotal(), System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 设备导入提交
   */
  @PostMapping("/import/commit")
  public ApiResponse<DeviceImportCommitResultVO> importCommit(@Valid @RequestBody List<DeviceImportDTO> dtos) {
    log.info("REQ POST /api/v1/devices/import/commit count={}", dtos.size());
    long start = System.currentTimeMillis();

    var rows = converter.toImportRows(dtos);
    var result = deviceService.importCommit(rows);
    var vo = converter.toImportCommitResultVO(result);

    log.info("RES POST /api/v1/devices/import/commit successCount={} costMs={}", result.getSuccessCount(), System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 设备导出
   */
  @GetMapping("/export")
  public ApiResponse<DeviceExportResultVO> export(
      @RequestParam(required = false) Long productId,
      @RequestParam(required = false) String keyword) {
    log.info("REQ GET /api/v1/devices/export productId={} keyword={}", productId, keyword);
    long start = System.currentTimeMillis();

    var result = deviceService.exportDevices(productId, keyword);
    var vo = converter.toExportResultVO(result);

    log.info("RES GET /api/v1/devices/export fileName={} costMs={}", result.getFileName(), System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 获取设备遥测数据
   */
  @GetMapping("/{id}/telemetry/latest")
  public ApiResponse<List<DeviceTelemetryVO>> getTelemetry(@PathVariable Long id) {
    log.info("REQ GET /api/v1/devices/{}/telemetry/latest", id);
    long start = System.currentTimeMillis();

    var result = deviceService.getDeviceTelemetry(id);
    var vos = converter.toTelemetryVOs(result);

    log.info("RES GET /api/v1/devices/{}/telemetry/latest count={} costMs={}", id, vos.size(), System.currentTimeMillis() - start);
    return ApiResponse.success(vos);
  }
}
