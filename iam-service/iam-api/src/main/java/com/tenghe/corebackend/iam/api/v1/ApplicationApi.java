package com.tenghe.corebackend.iam.api.v1;

import com.tenghe.corebackend.iam.api.dto.application.ApplicationDetailResponse;
import com.tenghe.corebackend.iam.api.dto.application.ApplicationListItem;
import com.tenghe.corebackend.iam.api.dto.application.CreateApplicationRequest;
import com.tenghe.corebackend.iam.api.dto.application.UpdateApplicationRequest;
import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.common.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "应用")
public interface ApplicationApi {

  @Operation(summary = "查询应用分页")
  @GetMapping
  ApiResponse<PageResponse<ApplicationListItem>> listApplications(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "keyword", required = false) String keyword);

  @Operation(summary = "查询应用详情")
  @GetMapping("/{applicationId}")
  ApiResponse<ApplicationDetailResponse> getApplication(@PathVariable("applicationId") Long applicationId);

  @Operation(summary = "创建应用")
  @PostMapping
  ApiResponse<Long> createApplication(@RequestBody CreateApplicationRequest request);

  @Operation(summary = "更新应用")
  @PutMapping("/{applicationId}")
  ApiResponse<Void> updateApplication(
      @PathVariable("applicationId") Long applicationId,
      @RequestBody UpdateApplicationRequest request);

  @Operation(summary = "删除应用")
  @DeleteMapping("/{applicationId}")
  ApiResponse<Void> deleteApplication(@PathVariable("applicationId") Long applicationId);
}
