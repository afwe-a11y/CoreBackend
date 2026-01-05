package com.tenghe.corebackend.iam.api.v1;

import com.tenghe.corebackend.iam.api.dto.common.ApiResponse;
import com.tenghe.corebackend.iam.api.dto.common.PageResponse;
import com.tenghe.corebackend.iam.api.dto.user.UserDetailResponse;
import com.tenghe.corebackend.iam.api.dto.user.UserListItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "用户")
public interface UserApi {

  @Operation(summary = "查询用户分页")
  @GetMapping
  ApiResponse<PageResponse<UserListItem>> listUsers(
      @RequestParam(value = "page", required = false) Integer page,
      @RequestParam(value = "size", required = false) Integer size,
      @RequestParam(value = "keyword", required = false) String keyword);

  @Operation(summary = "查询用户详情")
  @GetMapping("/{userId}")
  ApiResponse<UserDetailResponse> getUser(@PathVariable("userId") Long userId);

  @Operation(summary = "删除用户")
  @DeleteMapping("/{userId}")
  ApiResponse<Void> deleteUser(@PathVariable("userId") Long userId);
}
