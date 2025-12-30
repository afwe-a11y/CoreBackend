package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.AssignAdminCommand;
import com.tenghe.corebackend.application.command.CreateOrganizationCommand;
import com.tenghe.corebackend.application.command.UpdateOrganizationCommand;
import com.tenghe.corebackend.application.service.result.DeleteOrganizationInfoResult;
import com.tenghe.corebackend.application.service.result.OrganizationDetailResult;
import com.tenghe.corebackend.application.service.result.OrganizationListItemResult;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.UserSummaryResult;
import java.util.List;

public interface OrganizationApplicationService {

    PageResult<OrganizationListItemResult> listOrganizations(String keyword, Integer page, Integer size);

    Long createOrganization(CreateOrganizationCommand command);

    OrganizationDetailResult getOrganizationDetail(Long organizationId);

    void updateOrganization(UpdateOrganizationCommand command);

    void deleteOrganization(Long organizationId);

    DeleteOrganizationInfoResult getDeleteInfo(Long organizationId);

    List<UserSummaryResult> searchAdminCandidates(Long organizationId, String keyword);

    void assignAdmin(AssignAdminCommand command);
}
