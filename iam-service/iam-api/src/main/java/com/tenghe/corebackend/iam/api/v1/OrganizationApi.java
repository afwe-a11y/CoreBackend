package com.tenghe.corebackend.iam.api.v1;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.common.PageResponse;
import com.tenghe.corebackend.iam.api.dto.organization.CreateOrganizationRequest;
import com.tenghe.corebackend.iam.api.dto.organization.OrganizationDetailResponse;
import com.tenghe.corebackend.iam.api.dto.organization.OrganizationListItem;
import com.tenghe.corebackend.iam.api.dto.organization.UpdateOrganizationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "组织")
public interface OrganizationApi {

  @Operation(summary = "查询组织分页")
  @GetMapping
  ApiResponse<PageResponse<OrganizationListItem>> listOrganizations(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "keyword", required = false) String keyword);

  @Operation(summary = "查询组织详情")
  @GetMapping("/{organizationId}")
  ApiResponse<OrganizationDetailResponse> getOrganization(@PathVariable("organizationId") Long organizationId);

  @Operation(summary = "创建组织")
  @PostMapping
  ApiResponse<Long> createOrganization(@RequestBody CreateOrganizationRequest request);

  @Operation(summary = "更新组织")
  @PutMapping("/{organizationId}")
  ApiResponse<Void> updateOrganization(
      @PathVariable("organizationId") Long organizationId,
      @RequestBody UpdateOrganizationRequest request);

  @Operation(summary = "删除组织")
  @DeleteMapping("/{organizationId}")
  ApiResponse<Void> deleteOrganization(@PathVariable("organizationId") Long organizationId);
}
