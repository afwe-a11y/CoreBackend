package com.tenghe.corebackend.kronos.controller.web;

import com.tenghe.corebackend.kronos.api.common.ApiResponse;
import com.tenghe.corebackend.kronos.api.common.PageResponse;
import com.tenghe.corebackend.kronos.api.dto.devicemodel.DeviceModelCreateDTO;
import com.tenghe.corebackend.kronos.api.dto.devicemodel.ModelPointImportDTO;
import com.tenghe.corebackend.kronos.api.vo.devicemodel.DeviceModelDetailVO;
import com.tenghe.corebackend.kronos.api.vo.devicemodel.DeviceModelListVO;
import com.tenghe.corebackend.kronos.application.DeviceModelApplicationService;
import com.tenghe.corebackend.kronos.controller.converter.DeviceModelVOConverter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备模型控制器（BFF 层）。
 * 对接前端，编排调用 device-service。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/device-models")
@RequiredArgsConstructor
public class DeviceModelController {

  private final DeviceModelApplicationService deviceModelService;
  private final DeviceModelVOConverter converter = DeviceModelVOConverter.INSTANCE;

  /**
   * 分页查询设备模型列表
   */
  @GetMapping
  public ApiResponse<PageResponse<DeviceModelListVO>> list(
      @RequestParam(defaultValue = "1") Integer page,
      @RequestParam(defaultValue = "20") Integer size) {
    log.info("REQ GET /api/v1/device-models page={} size={}", page, size);
    long start = System.currentTimeMillis();

    var result = deviceModelService.listModels(page, size);
    var vos = result.getItems().stream().map(converter::toListVO).toList();
    var pageResponse = PageResponse.of(vos, result.getTotal(), result.getPage(), result.getSize());

    log.info("RES GET /api/v1/device-models total={} costMs={}", result.getTotal(), System.currentTimeMillis() - start);
    return ApiResponse.success(pageResponse);
  }

  /**
   * 查询设备模型详情
   */
  @GetMapping("/{id}")
  public ApiResponse<DeviceModelDetailVO> get(@PathVariable Long id) {
    log.info("REQ GET /api/v1/device-models/{}", id);
    long start = System.currentTimeMillis();

    var result = deviceModelService.getModel(id);
    var vo = converter.toDetailVO(result);

    log.info("RES GET /api/v1/device-models/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 创建设备模型
   */
  @PostMapping
  public ApiResponse<DeviceModelDetailVO> create(@Valid @RequestBody DeviceModelCreateDTO dto) {
    log.info("REQ POST /api/v1/device-models identifier={}", dto.getIdentifier());
    long start = System.currentTimeMillis();

    var points = converter.toPointDtos(dto.getPoints());
    var result = deviceModelService.createModel(
        dto.getIdentifier(), dto.getName(), dto.getSource(), dto.getParentModelId(), points);
    var vo = converter.toDetailVO(result);

    log.info("RES POST /api/v1/device-models id={} costMs={}", result.getId(), System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }

  /**
   * 删除设备模型
   */
  @DeleteMapping("/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id) {
    log.info("REQ DELETE /api/v1/device-models/{}", id);
    long start = System.currentTimeMillis();

    deviceModelService.deleteModel(id);

    log.info("RES DELETE /api/v1/device-models/{} costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success();
  }

  /**
   * 导入设备模型点位
   */
  @PostMapping("/{id}/points/import")
  public ApiResponse<DeviceModelDetailVO> importPoints(
      @PathVariable Long id,
      @Valid @RequestBody List<ModelPointImportDTO> dtos) {
    log.info("REQ POST /api/v1/device-models/{}/points/import count={}", id, dtos.size());
    long start = System.currentTimeMillis();

    var points = converter.toPointDtos(dtos);
    var result = deviceModelService.importPoints(id, points);
    var vo = converter.toDetailVO(result);

    log.info("RES POST /api/v1/device-models/{}/points/import costMs={}", id, System.currentTimeMillis() - start);
    return ApiResponse.success(vo);
  }
}
