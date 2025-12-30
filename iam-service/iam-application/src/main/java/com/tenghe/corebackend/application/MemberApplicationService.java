package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.CreateInternalMemberCommand;
import com.tenghe.corebackend.application.command.LinkExternalMemberCommand;
import com.tenghe.corebackend.application.command.UpdateInternalMemberCommand;
import com.tenghe.corebackend.application.service.result.ExternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.InternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.UserSummaryResult;
import java.util.List;

public interface MemberApplicationService {

    PageResult<InternalMemberListItemResult> listInternalMembers(Long organizationId, String keyword, Integer page, Integer size);

    Long createInternalMember(Long organizationId, CreateInternalMemberCommand command);

    void updateInternalMember(Long organizationId, UpdateInternalMemberCommand command);

    void disableInternalMember(Long organizationId, Long userId);

    void deleteInternalMember(Long organizationId, Long userId);

    PageResult<ExternalMemberListItemResult> listExternalMembers(Long organizationId, String keyword, Integer page, Integer size);

    List<UserSummaryResult> searchLinkCandidates(Long organizationId, String keyword);

    void linkExternalMember(Long organizationId, LinkExternalMemberCommand command);

    void unlinkExternalMember(Long organizationId, Long userId);
}
