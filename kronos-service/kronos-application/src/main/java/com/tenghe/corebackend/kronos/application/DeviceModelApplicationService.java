package com.tenghe.corebackend.kronos.application;

import com.tenghe.corebackend.kronos.application.exception.BusinessException;
import com.tenghe.corebackend.kronos.application.exception.ErrorCode;
import com.tenghe.corebackend.kronos.interfaces.downstream.DeviceModelServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.CreateDeviceModelRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelDetailResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.DeviceModelPointDto;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.model.ImportDeviceModelPointsRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 设备模型应用服务（BFF 编排层）。
 * 负责调用 device-service 并进行数据编排。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceModelApplicationService {

  private final DeviceModelServiceClient deviceModelClient;

  /**
   * 分页查询设备模型列表
   *
   * @param page 页码
   * @param size 每页数量
   * @return 分页结果
   */
  public DeviceServicePageResponse<DeviceModelListItem> listModels(Integer page, Integer size) {
    log.info("查询设备模型列表: page={}, size={}", page, size);
    var response = deviceModelClient.listModels(page, size);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    return response.getData();
  }

  /**
   * 查询设备模型详情
   *
   * @param modelId 模型ID
   * @return 模型详情
   */
  public DeviceModelDetailResponse getModel(Long modelId) {
    log.info("查询设备模型详情: modelId={}", modelId);
    var response = deviceModelClient.getModel(modelId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DEVICE_MODEL_NOT_FOUND, response.getMessage());
    }
    return response.getData();
  }

  /**
   * 创建设备模型
   *
   * @param identifier    标识符
   * @param name          名称
   * @param source        来源
   * @param parentModelId 父模型ID
   * @param points        点位列表
   * @return 创建结果
   */
  public DeviceModelDetailResponse createModel(String identifier, String name, String source,
      Long parentModelId, List<DeviceModelPointDto> points) {
    log.info("创建设备模型: identifier={}, name={}, source={}", identifier, name, source);
    var request = CreateDeviceModelRequest.builder()
        .identifier(identifier)
        .name(name)
        .source(source)
        .parentModelId(parentModelId)
        .points(points)
        .build();
    var response = deviceModelClient.createModel(request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备模型创建成功: id={}", response.getData().getId());
    return response.getData();
  }

  /**
   * 删除设备模型
   *
   * @param modelId 模型ID
   */
  public void deleteModel(Long modelId) {
    log.info("删除设备模型: modelId={}", modelId);
    var response = deviceModelClient.deleteModel(modelId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备模型删除成功: modelId={}", modelId);
  }

  /**
   * 导入设备模型点位
   *
   * @param modelId 模型ID
   * @param points  点位列表
   * @return 导入结果
   */
  public DeviceModelDetailResponse importPoints(Long modelId, List<DeviceModelPointDto> points) {
    log.info("导入设备模型点位: modelId={}, pointsCount={}", modelId, points != null ? points.size() : 0);
    var request = ImportDeviceModelPointsRequest.builder()
        .points(points)
        .build();
    var response = deviceModelClient.importPoints(modelId, request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备模型点位导入成功: modelId={}", modelId);
    return response.getData();
  }
}
