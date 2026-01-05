package com.tenghe.corebackend.iam.controller.web;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.common.PageResponse;
import com.tenghe.corebackend.iam.api.dto.organization.*;
import com.tenghe.corebackend.iam.api.dto.user.UserSummary;
import com.tenghe.corebackend.iam.application.OrganizationApplicationService;
import com.tenghe.corebackend.iam.application.command.AssignAdminCommand;
import com.tenghe.corebackend.iam.application.command.CreateOrganizationCommand;
import com.tenghe.corebackend.iam.application.command.UpdateOrganizationCommand;
import com.tenghe.corebackend.iam.application.service.result.OrganizationDetailResult;
import com.tenghe.corebackend.iam.application.service.result.OrganizationListItemResult;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import com.tenghe.corebackend.iam.application.service.result.UserSummaryResult;
import com.tenghe.corebackend.iam.model.enums.OrganizationStatusEnum;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 组织管理 HTTP 入口，面向服务间调用。
 */
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      .withZone(ZoneId.systemDefault());

  private final OrganizationApplicationService organizationService;

  public OrganizationController(OrganizationApplicationService organizationService) {
    this.organizationService = organizationService;
  }

  /**
   * 分页查询组织列表。
   */
  @GetMapping
  public ApiResponse<PageResponse<OrganizationListItem>> listOrganizations(
      @RequestParam(value = "keyword", required = false) String keyword,
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size) {
    PageResult<OrganizationListItemResult> result = organizationService.listOrganizations(keyword, page, size);
    List<OrganizationListItem> items = result.getItems().stream()
        .map(this::toListItem)
        .toList();
    PageResponse<OrganizationListItem> response = new PageResponse<>(items, result.getTotal(), result.getPage(),
        result.getSize());
    return ApiResponse.ok(response);
  }

  /**
   * 创建组织。
   */
  @PostMapping
  public ApiResponse<CreateOrganizationResponse> createOrganization(@RequestBody CreateOrganizationRequest request) {
    CreateOrganizationCommand command = new CreateOrganizationCommand();
    command.setName(request.getName());
    command.setCode(request.getCode());
    command.setDescription(request.getDescription());
    command.setAppIds(request.getAppIds());
    Long id = organizationService.createOrganization(command);
    return ApiResponse.ok(new CreateOrganizationResponse(String.valueOf(id)));
  }

  /**
   * 获取组织详情。
   */
  @GetMapping("/{organizationId}")
  public ApiResponse<OrganizationDetailResponse> getOrganizationDetail(@PathVariable("organizationId") Long organizationId) {
    OrganizationDetailResult result = organizationService.getOrganizationDetail(organizationId);
    OrganizationDetailResponse response = new OrganizationDetailResponse();
    response.setId(String.valueOf(result.getId()));
    response.setName(result.getName());
    response.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
    response.setDescription(result.getDescription());
    response.setAppIds(result.getAppIds());
    response.setStatus(result.getStatus());
    response.setContactName(result.getContactName());
    response.setContactPhone(result.getContactPhone());
    response.setContactEmail(result.getContactEmail());
    return ApiResponse.ok(response);
  }

  /**
   * 更新组织信息。
   */
  @PutMapping("/{organizationId}")
  public ApiResponse<Void> updateOrganization(
      @PathVariable("organizationId") Long organizationId,
      @RequestBody UpdateOrganizationRequest request) {
    UpdateOrganizationCommand command = new UpdateOrganizationCommand();
    command.setOrganizationId(organizationId);
    command.setName(request.getName());
    command.setDescription(request.getDescription());
    command.setAppIds(request.getAppIds());
    command.setStatus(request.getStatus());
    command.setContactName(request.getContactName());
    command.setContactPhone(request.getContactPhone());
    command.setContactEmail(request.getContactEmail());
    organizationService.updateOrganization(command);
    return ApiResponse.ok(null);
  }

  /**
   * 删除组织。
   */
  @DeleteMapping("/{organizationId}")
  public ApiResponse<Void> deleteOrganization(@PathVariable("organizationId") Long organizationId) {
    organizationService.deleteOrganization(organizationId);
    return ApiResponse.ok(null);
  }

  /**
   * 获取组织删除提示信息。
   */
  @GetMapping("/{organizationId}/delete-info")
  public ApiResponse<DeleteOrganizationInfoResponse> getDeleteInfo(@PathVariable("organizationId") Long organizationId) {
    com.tenghe.corebackend.iam.application.service.result.DeleteOrganizationInfoResult result =
        organizationService.getDeleteInfo(organizationId);
    DeleteOrganizationInfoResponse response = new DeleteOrganizationInfoResponse();
    response.setName(result.getName());
    response.setUserCount(result.getUserCount());
    return ApiResponse.ok(response);
  }

  /**
   * 查询组织管理员候选人。
   */
  @GetMapping("/{organizationId}/admin/search")
  public ApiResponse<List<UserSummary>> searchAdminCandidates(
      @PathVariable("organizationId") Long organizationId,
      @RequestParam(value = "keyword", required = false) String keyword) {
    List<UserSummaryResult> results = organizationService.searchAdminCandidates(organizationId, keyword);
    List<UserSummary> users = results.stream().map(this::toUserSummary).toList();
    return ApiResponse.ok(users);
  }

  /**
   * 指派组织管理员。
   */
  @PostMapping("/{organizationId}/admin/assign")
  public ApiResponse<Void> assignAdmin(
      @PathVariable("organizationId") Long organizationId,
      @RequestBody AssignAdminRequest request) {
    AssignAdminCommand command = new AssignAdminCommand();
    command.setOrganizationId(organizationId);
    command.setUserId(request.getUserId());
    organizationService.assignAdmin(command);
    return ApiResponse.ok(null);
  }

  private OrganizationListItem toListItem(OrganizationListItemResult result) {
    OrganizationListItem item = new OrganizationListItem();
    item.setId(String.valueOf(result.getId()));
    item.setName(result.getName());
    item.setInternalMemberCount(result.getInternalMemberCount());
    item.setExternalMemberCount(result.getExternalMemberCount());
    item.setPrimaryAdminDisplay(result.getPrimaryAdminDisplay());
    item.setStatus(formatStatus(result.getStatus()));
    item.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
    return item;
  }

  private String formatStatus(String status) {
    if (status == null) {
      return null;
    }
    OrganizationStatusEnum orgStatus = OrganizationStatusEnum.fromValue(status);
    if (orgStatus == null) {
      return status;
    }
    return orgStatus == OrganizationStatusEnum.NORMAL ? "正常" : "停用";
  }

  private UserSummary toUserSummary(UserSummaryResult result) {
    UserSummary summary = new UserSummary();
    summary.setId(String.valueOf(result.getId()));
    summary.setUsername(result.getUsername());
    summary.setName(result.getName());
    summary.setPhone(result.getPhone());
    summary.setEmail(result.getEmail());
    return summary;
  }
}
