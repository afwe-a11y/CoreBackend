package com.tenghe.corebackend.api.feign;

import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.api.dto.common.PageResponse;
import com.tenghe.corebackend.api.dto.user.CreateUserRequest;
import com.tenghe.corebackend.api.dto.user.ToggleUserStatusRequest;
import com.tenghe.corebackend.api.dto.user.UpdateUserRequest;
import com.tenghe.corebackend.api.dto.user.UserDetailResponse;
import com.tenghe.corebackend.api.dto.user.UserListItem;
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
 * IAM 用户服务 Feign Client
 * <p>
 * 供上层微服务调用 iam-service 的用户管理功能。
 * </p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>用户名：必填，≤20字符，全局唯一，仅允许字母+数字</li>
 *   <li>邮箱：必填，有效格式，全局唯一</li>
 *   <li>创建成功后：系统通过邮件发送初始密码</li>
 *   <li>删除：仅支持软删除（逻辑删除）</li>
 * </ul>
 */
@FeignClient(name = "iam-service", path = "/api/users", contextId = "iamUserClient")
public interface IamUserClient {

    /**
     * 分页查询用户列表
     * <p>
     * 支持按用户名/ID、状态、组织、角色等条件筛选。
     * </p>
     * 
     * @param keyword 搜索关键词（用户名/姓名模糊匹配）
     * @param status 状态筛选：NORMAL(正常) / DISABLED(停用)
     * @param organizationIds 组织ID列表筛选
     * @param roleCodes 角色编码列表筛选
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping
    ApiResponse<PageResponse<UserListItem>> listUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "organizationIds", required = false) List<Long> organizationIds,
            @RequestParam(value = "roleCodes", required = false) List<String> roleCodes,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size);

    /**
     * 创建用户
     * <p>
     * 创建新用户账号，系统自动生成初始密码并通过邮件发送给用户。
     * </p>
     * 
     * @param request 创建请求，包含用户名、姓名、手机、邮箱、组织ID列表、角色选择
     * @return 新创建的用户ID
     */
    @PostMapping
    ApiResponse<String> createUser(@RequestBody CreateUserRequest request);

    /**
     * 获取用户详情
     * 
     * @param userId 用户ID
     * @return 用户详情，包含基本信息、关联组织、角色选择
     */
    @GetMapping("/{userId}")
    ApiResponse<UserDetailResponse> getUserDetail(@PathVariable("userId") Long userId);

    /**
     * 更新用户信息
     * <p>
     * 用户名不可修改。组织变更时，自动移除被移除组织下的所有角色授权。
     * </p>
     * 
     * @param userId 用户ID
     * @param request 更新请求，包含姓名、手机、邮箱、组织ID列表、角色选择
     * @return 空响应
     */
    @PutMapping("/{userId}")
    ApiResponse<Void> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequest request);

    /**
     * 切换用户状态（启用/禁用）
     * <p>
     * 禁用用户后，该用户将被全局拦截，无法访问任何功能。
     * </p>
     * 
     * @param userId 用户ID
     * @param request 状态请求，包含目标状态
     * @return 空响应
     */
    @PutMapping("/{userId}/status")
    ApiResponse<Void> toggleUserStatus(@PathVariable("userId") Long userId, @RequestBody ToggleUserStatusRequest request);

    /**
     * 删除用户（软删除）
     * <p>
     * 执行逻辑删除，同时清理用户的组织成员关系和角色授权。
     * </p>
     * 
     * @param userId 用户ID
     * @return 空响应
     */
    @DeleteMapping("/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable("userId") Long userId);
}
