package com.tenghe.corebackend.iam.controller.web;

import com.tenghe.corebackend.iam.api.dto.application.ApplicationDetailResponse;
import com.tenghe.corebackend.iam.api.dto.application.ApplicationListItem;
import com.tenghe.corebackend.iam.api.dto.application.CreateApplicationRequest;
import com.tenghe.corebackend.iam.api.dto.application.UpdateApplicationRequest;
import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.common.PageResponse;
import com.tenghe.corebackend.iam.application.ApplicationApplicationService;
import com.tenghe.corebackend.iam.application.command.CreateApplicationCommand;
import com.tenghe.corebackend.iam.application.command.UpdateApplicationCommand;
import com.tenghe.corebackend.iam.application.service.result.ApplicationDetailResult;
import com.tenghe.corebackend.iam.application.service.result.ApplicationListItemResult;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 应用管理 HTTP 入口，面向服务间调用。
 */
@RestController
@RequestMapping("/api/applications")
public class ApplicationController {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      .withZone(ZoneId.systemDefault());

  private final ApplicationApplicationService applicationService;

  public ApplicationController(ApplicationApplicationService applicationService) {
    this.applicationService = applicationService;
  }

  /**
   * 分页查询应用列表。
   */
  @GetMapping
  public ApiResponse<PageResponse<ApplicationListItem>> listApplications(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    PageResult<ApplicationListItemResult> result = applicationService.listApplications(keyword, page, size);
    List<ApplicationListItem> items = result.getItems().stream()
        .map(this::toListItem)
        .toList();
    PageResponse<ApplicationListItem> response = new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize());
    return ApiResponse.ok(response);
  }

  /**
   * 创建应用。
   */
  @PostMapping
  public ApiResponse<String> createApplication(@RequestBody CreateApplicationRequest request) {
    CreateApplicationCommand command = new CreateApplicationCommand();
    command.setAppName(request.getAppName());
    command.setAppCode(request.getAppCode());
    command.setDescription(request.getDescription());
    command.setPermissionIds(request.getPermissionIds());
    Long id = applicationService.createApplication(command);
    return ApiResponse.ok(String.valueOf(id));
  }

  /**
   * 获取应用详情。
   */
  @GetMapping("/{appId}")
  public ApiResponse<ApplicationDetailResponse> getApplicationDetail(@PathVariable("appId") Long appId) {
    ApplicationDetailResult result = applicationService.getApplicationDetail(appId);
    ApplicationDetailResponse response = new ApplicationDetailResponse();
    response.setId(String.valueOf(result.getId()));
    response.setAppName(result.getAppName());
    response.setAppCode(result.getAppCode());
    response.setDescription(result.getDescription());
    response.setStatus(formatStatus(result.getStatus()));
    response.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
    response.setPermissionIds(result.getPermissionIds());
    return ApiResponse.ok(response);
  }

  /**
   * 更新应用信息。
   */
  @PutMapping("/{appId}")
  public ApiResponse<Void> updateApplication(
      @PathVariable("appId") Long appId,
      @RequestBody UpdateApplicationRequest request) {
    UpdateApplicationCommand command = new UpdateApplicationCommand();
    command.setAppId(appId);
    command.setAppName(request.getAppName());
    command.setDescription(request.getDescription());
    command.setStatus(request.getStatus());
    command.setPermissionIds(request.getPermissionIds());
    applicationService.updateApplication(command);
    return ApiResponse.ok(null);
  }

  /**
   * 删除应用。
   */
  @DeleteMapping("/{appId}")
  public ApiResponse<Void> deleteApplication(@PathVariable("appId") Long appId) {
    applicationService.deleteApplication(appId);
    return ApiResponse.ok(null);
  }

  private ApplicationListItem toListItem(ApplicationListItemResult result) {
    ApplicationListItem item = new ApplicationListItem();
    item.setId(String.valueOf(result.getId()));
    item.setAppName(result.getAppName());
    item.setAppCode(result.getAppCode());
    item.setDescription(result.getDescription());
    item.setStatus(formatStatus(result.getStatus()));
    item.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
    return item;
  }

  private String formatStatus(String status) {
    if (status == null) {
      return null;
    }
    if ("ENABLED".equals(status)) {
      return "启用";
    }
    if ("DISABLED".equals(status)) {
      return "停用";
    }
    return status;
  }
}
