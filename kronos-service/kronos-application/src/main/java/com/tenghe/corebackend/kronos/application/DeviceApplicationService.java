package com.tenghe.corebackend.kronos.application;

import com.tenghe.corebackend.kronos.application.exception.BusinessException;
import com.tenghe.corebackend.kronos.application.exception.ErrorCode;
import com.tenghe.corebackend.kronos.interfaces.downstream.DeviceServiceClient;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.DeviceServicePageResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.CreateDeviceRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.CreateDeviceResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceExportResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportCommitRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportCommitResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportPreviewRequest;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportPreviewResponse;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceImportRowDto;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceListItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.DeviceTelemetryItem;
import com.tenghe.corebackend.kronos.interfaces.downstream.dto.device.UpdateDeviceRequest;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 设备应用服务（BFF 编排层）。
 * 负责调用 device-service 并进行数据编排。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeviceApplicationService {

  private final DeviceServiceClient deviceClient;

  /**
   * 分页查询设备列表
   *
   * @param productId 产品ID
   * @param keyword   关键词
   * @param page      页码
   * @param size      每页数量
   * @return 分页结果
   */
  public DeviceServicePageResponse<DeviceListItem> listDevices(Long productId, String keyword,
      Integer page, Integer size) {
    log.info("查询设备列表: productId={}, keyword={}, page={}, size={}", productId, keyword, page, size);
    var response = deviceClient.listDevices(productId, keyword, page, size);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    return response.getData();
  }

  /**
   * 创建设备
   *
   * @param name              名称
   * @param productId         产品ID
   * @param deviceKey         设备Key
   * @param gatewayId         网关ID
   * @param dynamicAttributes 动态属性
   * @return 创建结果
   */
  public CreateDeviceResponse createDevice(String name, Long productId, String deviceKey,
      Long gatewayId, Map<String, Object> dynamicAttributes) {
    log.info("创建设备: name={}, productId={}, gatewayId={}", name, productId, gatewayId);
    var request = CreateDeviceRequest.builder()
        .name(name)
        .productId(productId)
        .deviceKey(deviceKey)
        .gatewayId(gatewayId)
        .dynamicAttributes(dynamicAttributes)
        .build();
    var response = deviceClient.createDevice(request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备创建成功: id={}", response.getData().getId());
    return response.getData();
  }

  /**
   * 更新设备
   *
   * @param deviceId          设备ID
   * @param name              名称
   * @param gatewayId         网关ID
   * @param dynamicAttributes 动态属性
   */
  public void updateDevice(Long deviceId, String name, Long gatewayId,
      Map<String, Object> dynamicAttributes) {
    log.info("更新设备: deviceId={}", deviceId);
    var request = UpdateDeviceRequest.builder()
        .name(name)
        .gatewayId(gatewayId)
        .dynamicAttributes(dynamicAttributes)
        .build();
    var response = deviceClient.updateDevice(deviceId, request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备更新成功: deviceId={}", deviceId);
  }

  /**
   * 删除设备
   *
   * @param deviceId 设备ID
   */
  public void deleteDevice(Long deviceId) {
    log.info("删除设备: deviceId={}", deviceId);
    var response = deviceClient.deleteDevice(deviceId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备删除成功: deviceId={}", deviceId);
  }

  /**
   * 设备导入预览
   *
   * @param rows 导入行
   * @return 预览结果
   */
  public DeviceImportPreviewResponse importPreview(List<DeviceImportRowDto> rows) {
    log.info("设备导入预览: rowsCount={}", rows != null ? rows.size() : 0);
    var request = DeviceImportPreviewRequest.builder()
        .rows(rows)
        .build();
    var response = deviceClient.importPreview(request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备导入预览完成: total={}, createCount={}, updateCount={}, invalidCount={}",
        response.getData().getTotal(), response.getData().getCreateCount(),
        response.getData().getUpdateCount(), response.getData().getInvalidCount());
    return response.getData();
  }

  /**
   * 设备导入提交
   *
   * @param rows 导入行
   * @return 提交结果
   */
  public DeviceImportCommitResponse importCommit(List<DeviceImportRowDto> rows) {
    log.info("设备导入提交: rowsCount={}", rows != null ? rows.size() : 0);
    var request = DeviceImportCommitRequest.builder()
        .rows(rows)
        .build();
    var response = deviceClient.importCommit(request);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备导入提交完成: successCount={}, failureCount={}",
        response.getData().getSuccessCount(), response.getData().getFailureCount());
    return response.getData();
  }

  /**
   * 设备导出
   *
   * @param productId 产品ID
   * @param keyword   关键词
   * @return 导出结果
   */
  public DeviceExportResponse exportDevices(Long productId, String keyword) {
    log.info("设备导出: productId={}, keyword={}", productId, keyword);
    var response = deviceClient.exportDevices(productId, keyword);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("设备导出完成: fileName={}, rowsCount={}",
        response.getData().getFileName(),
        response.getData().getRows() != null ? response.getData().getRows().size() : 0);
    return response.getData();
  }

  /**
   * 获取设备遥测数据
   *
   * @param deviceId 设备ID
   * @return 遥测数据
   */
  public List<DeviceTelemetryItem> getDeviceTelemetry(Long deviceId) {
    log.info("获取设备遥测数据: deviceId={}", deviceId);
    var response = deviceClient.getDeviceTelemetry(deviceId);
    if (!response.isOk()) {
      throw new BusinessException(ErrorCode.DOWNSTREAM_SERVICE_ERROR, response.getMessage());
    }
    log.info("获取设备遥测数据成功: deviceId={}, itemsCount={}",
        deviceId, response.getData() != null ? response.getData().size() : 0);
    return response.getData();
  }
}
