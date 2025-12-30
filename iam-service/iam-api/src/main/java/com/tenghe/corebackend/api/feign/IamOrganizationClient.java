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
 * <p>
 * 供上层微服务调用 iam-service 的组织管理功能。
 * 组织是数据边界，定义了可使用的应用范围。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>组织名称：必填，1-50字符，全局唯一（包括所有状态）</li>
 *   <li>组织编码：必填，全局唯一，仅允许字母/数字/下划线</li>
 *   <li>可使用应用：组织可配置0..N个可使用的应用</li>
 *   <li>删除：仅支持软删除</li>
 * </ul>
 */
@FeignClient(name = "iam-service", path = "/api/organizations", contextId = "iamOrganizationClient")
public interface IamOrganizationClient {

    /**
     * 分页查询组织列表
     * 
     * @param keyword 搜索关键词（组织名称/编码模糊匹配）
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping
    ApiResponse<PageResponse<OrganizationListItem>> listOrganizations(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    /**
     * 创建组织
     * 
     * @param request 创建请求，包含名称、编码、描述、可使用应用ID列表
     * @return 创建响应，包含新组织ID
     */
    @PostMapping
    ApiResponse<CreateOrganizationResponse> createOrganization(@RequestBody CreateOrganizationRequest request);

    /**
     * 获取组织详情
     * 
     * @param organizationId 组织ID
     * @return 组织详情，包含基本信息、可使用应用、联系人信息
     */
    @GetMapping("/{organizationId}")
    ApiResponse<OrganizationDetailResponse> getOrganizationDetail(@PathVariable("organizationId") Long organizationId);

    /**
     * 更新组织信息
     * <p>
     * 移除应用时的风险规则：如果被移除的应用下有用户已授权的角色，需阻止移除。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param request 更新请求，包含名称、描述、可使用应用、状态、联系人信息
     * @return 空响应
     */
    @PutMapping("/{organizationId}")
    ApiResponse<Void> updateOrganization(@PathVariable("organizationId") Long organizationId, @RequestBody UpdateOrganizationRequest request);

    /**
     * 删除组织（软删除）
     * <p>
     * 执行逻辑删除，同时清理组织下的成员关系和角色授权。
     * </p>
     * 
     * @param organizationId 组织ID
     * @return 空响应
     */
    @DeleteMapping("/{organizationId}")
    ApiResponse<Void> deleteOrganization(@PathVariable("organizationId") Long organizationId);

    /**
     * 获取删除前的确认信息
     * <p>
     * 返回组织名称和关联的用户数量，供前端展示删除确认提示。
     * </p>
     * 
     * @param organizationId 组织ID
     * @return 删除信息，包含组织名称、用户数量
     */
    @GetMapping("/{organizationId}/delete-info")
    ApiResponse<DeleteOrganizationInfoResponse> getDeleteInfo(@PathVariable("organizationId") Long organizationId);

    /**
     * 搜索管理员候选人
     * <p>
     * 从平台用户中搜索可作为组织管理员的候选人。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param keyword 搜索关键词（用户名/姓名模糊匹配）
     * @return 候选用户列表
     */
    @GetMapping("/{organizationId}/admin/search")
    ApiResponse<List<UserSummary>> searchAdminCandidates(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam(value = "keyword", required = false) String keyword);

    /**
     * 指派组织管理员
     * <p>
     * 选择一个已存在的平台用户，授予其组织管理员角色。
     * </p>
     * 
     * @param organizationId 组织ID
     * @param request 指派请求，包含用户ID
     * @return 空响应
     */
    @PostMapping("/{organizationId}/admin/assign")
    ApiResponse<Void> assignAdmin(@PathVariable("organizationId") Long organizationId, @RequestBody AssignAdminRequest request);
}
