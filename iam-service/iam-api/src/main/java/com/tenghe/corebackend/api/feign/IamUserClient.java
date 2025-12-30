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
 * 供上层微服务调用用户管理功能
 */
@FeignClient(name = "iam-service", path = "/api/users", contextId = "iamUserClient")
public interface IamUserClient {

    /**
     * 分页查询用户列表
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
     */
    @PostMapping
    ApiResponse<String> createUser(@RequestBody CreateUserRequest request);

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    ApiResponse<UserDetailResponse> getUserDetail(@PathVariable("userId") Long userId);

    /**
     * 更新用户
     */
    @PutMapping("/{userId}")
    ApiResponse<Void> updateUser(@PathVariable("userId") Long userId, @RequestBody UpdateUserRequest request);

    /**
     * 切换用户状态
     */
    @PutMapping("/{userId}/status")
    ApiResponse<Void> toggleUserStatus(@PathVariable("userId") Long userId, @RequestBody ToggleUserStatusRequest request);

    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    ApiResponse<Void> deleteUser(@PathVariable("userId") Long userId);
}
