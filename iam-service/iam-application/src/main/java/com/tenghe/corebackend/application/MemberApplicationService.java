package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.CreateInternalMemberCommand;
import com.tenghe.corebackend.application.command.LinkExternalMemberCommand;
import com.tenghe.corebackend.application.command.UpdateInternalMemberCommand;
import com.tenghe.corebackend.application.service.result.CreateInternalMemberResult;
import com.tenghe.corebackend.application.service.result.ExternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.InternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.UserSummaryResult;
import java.util.List;

public interface MemberApplicationService {

    PageResult<InternalMemberListItemResult> listInternalMembers(Long organizationId, Integer page, Integer size);

    CreateInternalMemberResult createInternalMember(CreateInternalMemberCommand command);

    void updateInternalMember(UpdateInternalMemberCommand command);

    void disableInternalMember(Long organizationId, Long userId);

    void deleteInternalMember(Long organizationId, Long userId);

    PageResult<ExternalMemberListItemResult> listExternalMembers(Long organizationId, Integer page, Integer size);

    List<UserSummaryResult> searchExternalCandidates(Long organizationId, String keyword);

    void linkExternalMember(LinkExternalMemberCommand command);

    void unlinkExternalMember(Long organizationId, Long userId);
}
