package com.tenghe.corebackend.application;

import com.tenghe.corebackend.application.command.CreateApplicationCommand;
import com.tenghe.corebackend.application.command.UpdateApplicationCommand;
import com.tenghe.corebackend.application.service.result.ApplicationDetailResult;
import com.tenghe.corebackend.application.service.result.ApplicationListItemResult;
import com.tenghe.corebackend.application.service.result.PageResult;
import java.util.List;

public interface ApplicationApplicationService {

    PageResult<ApplicationListItemResult> listApplications(String keyword, Integer page, Integer size);

    Long createApplication(CreateApplicationCommand command);

    ApplicationDetailResult getApplicationDetail(Long applicationId);

    void updateApplication(UpdateApplicationCommand command);

    void deleteApplication(Long applicationId);

    List<ApplicationListItemResult> listAllApplications();
}
