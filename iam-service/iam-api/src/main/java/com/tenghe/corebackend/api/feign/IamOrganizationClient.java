package com.tenghe.corebackend.api.feign;

import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.api.dto.common.PageResponse;
import com.tenghe.corebackend.api.dto.organization.AssignAdminRequest;
import com.tenghe.corebackend.api.dto.organization.CreateOrganizationRequest;
import com.tenghe.corebackend.api.dto.organization.CreateOrganizationResponse;
import com.tenghe.corebackend.api.dto.organization.DeleteOrganizationInfoResponse;
import com.tenghe.corebackend.api.dto.organization.OrganizationDetailResponse;
import com.tenghe.corebackend.api.dto.organization.OrganizationListItem;
import com.tenghe.corebackend.api.dto.organization.UpdateOrganizationRequest;
import com.tenghe.corebackend.api.dto.user.UserSummary;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * IAM 组织服务 Feign Client
 * 供上层微服务调用组织管理功能
 */
@FeignClient(name = "iam-service", path = "/api/organizations", contextId = "iamOrganizationClient")
public interface IamOrganizationClient {

    /**
     * 分页查询组织列表
     */
    @GetMapping
    ApiResponse<PageResponse<OrganizationListItem>> listOrganizations(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    /**
     * 创建组织
     */
    @PostMapping
    ApiResponse<CreateOrganizationResponse> createOrganization(@RequestBody CreateOrganizationRequest request);

    /**
     * 获取组织详情
     */
    @GetMapping("/{organizationId}")
    ApiResponse<OrganizationDetailResponse> getOrganizationDetail(@PathVariable("organizationId") Long organizationId);

    /**
     * 更新组织
     */
    @PutMapping("/{organizationId}")
    ApiResponse<Void> updateOrganization(@PathVariable("organizationId") Long organizationId, @RequestBody UpdateOrganizationRequest request);

    /**
     * 删除组织
     */
    @DeleteMapping("/{organizationId}")
    ApiResponse<Void> deleteOrganization(@PathVariable("organizationId") Long organizationId);

    /**
     * 获取删除信息
     */
    @GetMapping("/{organizationId}/delete-info")
    ApiResponse<DeleteOrganizationInfoResponse> getDeleteInfo(@PathVariable("organizationId") Long organizationId);

    /**
     * 搜索管理员候选人
     */
    @GetMapping("/{organizationId}/admin/search")
    ApiResponse<List<UserSummary>> searchAdminCandidates(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam(value = "keyword", required = false) String keyword);

    /**
     * 指定组织管理员
     */
    @PostMapping("/{organizationId}/admin/assign")
    ApiResponse<Void> assignAdmin(@PathVariable("organizationId") Long organizationId, @RequestBody AssignAdminRequest request);
}
