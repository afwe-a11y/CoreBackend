package com.tenghe.corebackend.controller.web;

import com.tenghe.corebackend.api.dto.common.ApiResponse;
import com.tenghe.corebackend.api.dto.common.PageResponse;
import com.tenghe.corebackend.api.dto.member.CreateInternalMemberRequest;
import com.tenghe.corebackend.api.dto.member.CreateInternalMemberResponse;
import com.tenghe.corebackend.api.dto.member.ExternalMemberListItem;
import com.tenghe.corebackend.api.dto.member.InternalMemberListItem;
import com.tenghe.corebackend.api.dto.member.LinkExternalMemberRequest;
import com.tenghe.corebackend.api.dto.member.RoleSelectionDto;
import com.tenghe.corebackend.api.dto.member.UpdateInternalMemberRequest;
import com.tenghe.corebackend.api.dto.user.UserSummary;
import com.tenghe.corebackend.application.command.CreateInternalMemberCommand;
import com.tenghe.corebackend.application.command.LinkExternalMemberCommand;
import com.tenghe.corebackend.application.command.RoleSelectionCommand;
import com.tenghe.corebackend.application.command.UpdateInternalMemberCommand;
import com.tenghe.corebackend.application.MemberApplicationService;
import com.tenghe.corebackend.application.service.result.CreateInternalMemberResult;
import com.tenghe.corebackend.application.service.result.ExternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.InternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.UserSummaryResult;
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
@RequestMapping("/api/organizations/{organizationId}/members")
public class MemberController {
    private final MemberApplicationService memberService;

    public MemberController(MemberApplicationService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/internal")
    public ApiResponse<PageResponse<InternalMemberListItem>> listInternalMembers(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        PageResult<InternalMemberListItemResult> result = memberService.listInternalMembers(organizationId, page, size);
        List<InternalMemberListItem> items = result.getItems().stream()
                .map(this::toInternalMember)
                .toList();
        PageResponse<InternalMemberListItem> response = new PageResponse<>(items, result.getTotal(), result.getPage(),
                result.getSize());
        return ApiResponse.ok(response);
    }

    @PostMapping("/internal")
    public ApiResponse<CreateInternalMemberResponse> createInternalMember(
            @PathVariable("organizationId") Long organizationId,
            @RequestBody CreateInternalMemberRequest request) {
        CreateInternalMemberCommand command = new CreateInternalMemberCommand();
        command.setOrganizationId(organizationId);
        command.setUsername(request.getUsername());
        command.setName(request.getName());
        command.setPhone(request.getPhone());
        command.setEmail(request.getEmail());
        command.setOrganizationIds(request.getOrganizationIds());
        command.setStatus(request.getStatus());
        command.setAccountType(request.getAccountType());
        command.setRoleSelections(toRoleSelections(request.getRoleSelections()));
        CreateInternalMemberResult result = memberService.createInternalMember(command);
        return ApiResponse.ok(new CreateInternalMemberResponse(result.getUsername(), result.getPhone()));
    }

    @PutMapping("/internal/{userId}")
    public ApiResponse<Void> updateInternalMember(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("userId") Long userId,
            @RequestBody UpdateInternalMemberRequest request) {
        UpdateInternalMemberCommand command = new UpdateInternalMemberCommand();
        command.setOrganizationId(organizationId);
        command.setUserId(userId);
        command.setName(request.getName());
        command.setPhone(request.getPhone());
        command.setEmail(request.getEmail());
        command.setStatus(request.getStatus());
        command.setAccountType(request.getAccountType());
        memberService.updateInternalMember(command);
        return ApiResponse.ok(null);
    }

    @PostMapping("/internal/{userId}/disable")
    public ApiResponse<Void> disableInternalMember(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("userId") Long userId) {
        memberService.disableInternalMember(organizationId, userId);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/internal/{userId}")
    public ApiResponse<Void> deleteInternalMember(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("userId") Long userId) {
        memberService.deleteInternalMember(organizationId, userId);
        return ApiResponse.ok(null);
    }

    @GetMapping("/external")
    public ApiResponse<PageResponse<ExternalMemberListItem>> listExternalMembers(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size) {
        PageResult<ExternalMemberListItemResult> result = memberService.listExternalMembers(organizationId, page, size);
        List<ExternalMemberListItem> items = result.getItems().stream()
                .map(this::toExternalMember)
                .toList();
        PageResponse<ExternalMemberListItem> response = new PageResponse<>(items, result.getTotal(), result.getPage(),
                result.getSize());
        return ApiResponse.ok(response);
    }

    @GetMapping("/external/search")
    public ApiResponse<List<UserSummary>> searchExternalMembers(
            @PathVariable("organizationId") Long organizationId,
            @RequestParam(value = "keyword", required = false) String keyword) {
        List<UserSummaryResult> results = memberService.searchExternalCandidates(organizationId, keyword);
        List<UserSummary> users = results.stream().map(this::toUserSummary).toList();
        return ApiResponse.ok(users);
    }

    @PostMapping("/external")
    public ApiResponse<Void> linkExternalMember(
            @PathVariable("organizationId") Long organizationId,
            @RequestBody LinkExternalMemberRequest request) {
        LinkExternalMemberCommand command = new LinkExternalMemberCommand();
        command.setOrganizationId(organizationId);
        command.setUserId(request.getUserId());
        memberService.linkExternalMember(command);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/external/{userId}")
    public ApiResponse<Void> unlinkExternalMember(
            @PathVariable("organizationId") Long organizationId,
            @PathVariable("userId") Long userId) {
        memberService.unlinkExternalMember(organizationId, userId);
        return ApiResponse.ok(null);
    }

    private List<RoleSelectionCommand> toRoleSelections(List<RoleSelectionDto> selections) {
        if (selections == null) {
            return null;
        }
        List<RoleSelectionCommand> results = new ArrayList<>();
        for (RoleSelectionDto selection : selections) {
            RoleSelectionCommand command = new RoleSelectionCommand();
            command.setAppId(selection.getAppId());
            command.setRoleCode(selection.getRoleCode());
            results.add(command);
        }
        return results;
    }

    private InternalMemberListItem toInternalMember(InternalMemberListItemResult result) {
        InternalMemberListItem item = new InternalMemberListItem();
        item.setUsername(result.getUsername());
        item.setPhone(result.getPhone());
        item.setEmail(result.getEmail());
        item.setRoles(result.getRoles());
        item.setStatus(formatUserStatus(result.getStatus()));
        return item;
    }

    private ExternalMemberListItem toExternalMember(ExternalMemberListItemResult result) {
        ExternalMemberListItem item = new ExternalMemberListItem();
        item.setUsername(result.getUsername());
        item.setSourceOrganizationName(result.getSourceOrganizationName());
        item.setPhone(result.getPhone());
        item.setEmail(result.getEmail());
        return item;
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

    private String formatUserStatus(String status) {
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
