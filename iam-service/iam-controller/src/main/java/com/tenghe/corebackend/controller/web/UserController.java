package com.tenghe.corebackend.controller.web;

import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.api.dto.common.PageResponse;
import com.tenghe.corebackend.api.dto.member.RoleSelectionDto;
import com.tenghe.corebackend.api.dto.user.CreateUserRequest;
import com.tenghe.corebackend.api.dto.user.ToggleUserStatusRequest;
import com.tenghe.corebackend.api.dto.user.UpdateUserRequest;
import com.tenghe.corebackend.api.dto.user.UserDetailResponse;
import com.tenghe.corebackend.api.dto.user.UserListItem;
import com.tenghe.corebackend.application.command.CreateUserCommand;
import com.tenghe.corebackend.application.command.RoleSelectionCommand;
import com.tenghe.corebackend.application.command.UpdateUserCommand;
import com.tenghe.corebackend.application.command.UserQueryCommand;
import com.tenghe.corebackend.application.UserApplicationService;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.RoleSelectionResult;
import com.tenghe.corebackend.application.service.result.UserDetailResult;
import com.tenghe.corebackend.application.service.result.UserListItemResult;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            .withZone(ZoneId.systemDefault());

    private final UserApplicationService userService;

    public UserController(UserApplicationService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<PageResponse<UserListItem>> listUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "organizationIds", required = false) List<Long> organizationIds,
            @RequestParam(value = "roleCodes", required = false) List<String> roleCodes,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        UserQueryCommand query = new UserQueryCommand();
        query.setKeyword(keyword);
        query.setStatus(status);
        query.setOrganizationIds(organizationIds);
        query.setRoleCodes(roleCodes);
        query.setPage(page);
        query.setSize(size);
        PageResult<UserListItemResult> result = userService.listUsers(query);
        List<UserListItem> items = result.getItems().stream()
                .map(this::toListItem)
                .toList();
        PageResponse<UserListItem> response = new PageResponse<>(items, result.getTotal(), result.getPage(), result.getSize());
        return ApiResponse.ok(response);
    }

    @PostMapping
    public ApiResponse<String> createUser(@RequestBody CreateUserRequest request) {
        CreateUserCommand command = new CreateUserCommand();
        command.setUsername(request.getUsername());
        command.setName(request.getName());
        command.setPhone(request.getPhone());
        command.setEmail(request.getEmail());
        command.setOrganizationIds(request.getOrganizationIds());
        command.setRoleSelections(toRoleSelections(request.getRoleSelections()));
        Long id = userService.createUser(command);
        return ApiResponse.ok(String.valueOf(id));
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserDetailResponse> getUserDetail(@PathVariable("userId") Long userId) {
        UserDetailResult result = userService.getUserDetail(userId);
        UserDetailResponse response = new UserDetailResponse();
        response.setId(String.valueOf(result.getId()));
        response.setUsername(result.getUsername());
        response.setName(result.getName());
        response.setPhone(result.getPhone());
        response.setEmail(result.getEmail());
        response.setStatus(formatStatus(result.getStatus()));
        response.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
        response.setOrganizationIds(result.getOrganizationIds());
        response.setRoleSelections(toRoleSelectionDtos(result.getRoleSelections()));
        return ApiResponse.ok(response);
    }

    @PutMapping("/{userId}")
    public ApiResponse<Void> updateUser(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserRequest request) {
        UpdateUserCommand command = new UpdateUserCommand();
        command.setUserId(userId);
        command.setName(request.getName());
        command.setPhone(request.getPhone());
        command.setEmail(request.getEmail());
        command.setOrganizationIds(request.getOrganizationIds());
        command.setRoleSelections(toRoleSelections(request.getRoleSelections()));
        userService.updateUser(command);
        return ApiResponse.ok(null);
    }

    @PutMapping("/{userId}/status")
    public ApiResponse<Void> toggleUserStatus(
            @PathVariable("userId") Long userId,
            @RequestBody ToggleUserStatusRequest request) {
        userService.toggleUserStatus(userId, request.getStatus());
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/{userId}")
    public ApiResponse<Void> deleteUser(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.ok(null);
    }

    private List<RoleSelectionCommand> toRoleSelections(List<RoleSelectionDto> dtos) {
        if (dtos == null) {
            return null;
        }
        List<RoleSelectionCommand> results = new ArrayList<>();
        for (RoleSelectionDto dto : dtos) {
            RoleSelectionCommand cmd = new RoleSelectionCommand();
            cmd.setAppId(dto.getAppId());
            cmd.setRoleCode(dto.getRoleCode());
            results.add(cmd);
        }
        return results;
    }

    private List<RoleSelectionDto> toRoleSelectionDtos(List<RoleSelectionResult> results) {
        if (results == null) {
            return null;
        }
        List<RoleSelectionDto> dtos = new ArrayList<>();
        for (RoleSelectionResult result : results) {
            RoleSelectionDto dto = new RoleSelectionDto();
            dto.setAppId(result.getAppId());
            dto.setRoleCode(result.getRoleCode());
            dtos.add(dto);
        }
        return dtos;
    }

    private UserListItem toListItem(UserListItemResult result) {
        UserListItem item = new UserListItem();
        item.setId(String.valueOf(result.getId()));
        item.setUsername(result.getUsername());
        item.setName(result.getName());
        item.setPhone(result.getPhone());
        item.setEmail(result.getEmail());
        item.setStatus(formatStatus(result.getStatus()));
        item.setOrganizationNames(result.getOrganizationNames());
        item.setRoleNames(result.getRoleNames());
        item.setCreatedDate(result.getCreatedAt() == null ? null : DATE_FORMATTER.format(result.getCreatedAt()));
        return item;
    }

    private String formatStatus(String status) {
        if (status == null) {
            return null;
        }
        if ("NORMAL".equals(status)) {
            return "正常";
        }
        if ("DISABLED".equals(status)) {
            return "停用";
        }
        return status;
    }
}
