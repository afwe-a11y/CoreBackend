package com.tenghe.corebackend.infrastructure.persistence.repository;

import com.tenghe.corebackend.infrastructure.persistence.mapper.ApplicationTemplateMapper;
import com.tenghe.corebackend.infrastructure.persistence.po.ApplicationTemplatePo;
import com.tenghe.corebackend.interfaces.ApplicationRepositoryPort;
import com.tenghe.corebackend.model.Application;
import com.tenghe.corebackend.model.enums.ApplicationStatusEnum;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisApplicationRepository implements ApplicationRepositoryPort {
    private final ApplicationTemplateMapper applicationTemplateMapper;

    public MyBatisApplicationRepository(ApplicationTemplateMapper applicationTemplateMapper) {
        this.applicationTemplateMapper = applicationTemplateMapper;
    }

    @Override
    public Application save(Application application) {
        applicationTemplateMapper.insert(toPo(application));
        return application;
    }

    @Override
    public Application update(Application application) {
        applicationTemplateMapper.update(toPo(application));
        return application;
    }

    @Override
    public Application findById(Long id) {
        return toModel(applicationTemplateMapper.findById(id));
    }

    @Override
    public Application findByCode(String appCode) {
        return toModel(applicationTemplateMapper.findByCode(appCode));
    }

    @Override
    public List<Application> listAll() {
        return applicationTemplateMapper.listAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Application> findByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return applicationTemplateMapper.findByIds(ids).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public void softDeleteById(Long id) {
        applicationTemplateMapper.softDeleteById(id);
    }

    private Application toModel(ApplicationTemplatePo po) {
        if (po == null) {
            return null;
        }
        Application application = new Application();
        application.setId(po.getId());
        application.setAppCode(po.getTemplateCode());
        application.setAppName(po.getTemplateName());
        application.setDescription(po.getDescription());
        application.setStatus(fromDbStatus(po.getStatus()));
        application.setCreatedAt(po.getCreateTime());
        application.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
        return application;
    }

    private ApplicationTemplatePo toPo(Application application) {
        ApplicationTemplatePo po = new ApplicationTemplatePo();
        po.setId(application.getId());
        po.setTemplateCode(application.getAppCode());
        po.setTemplateName(application.getAppName());
        po.setDescription(application.getDescription());
        po.setStatus(toDbStatus(application.getStatus()));
        po.setCreateTime(application.getCreatedAt());
        po.setDeleted(application.isDeleted() ? 1 : 0);
        return po;
    }

    private ApplicationStatusEnum fromDbStatus(String status) {
        if (status == null) {
            return null;
        }
        if ("NORMAL".equalsIgnoreCase(status)) {
            return ApplicationStatusEnum.ENABLED;
        }
        if ("DISABLED".equalsIgnoreCase(status)) {
            return ApplicationStatusEnum.DISABLED;
        }
        return ApplicationStatusEnum.fromValue(status);
    }

    private String toDbStatus(ApplicationStatusEnum status) {
        if (status == null) {
            return null;
        }
        if (status == ApplicationStatusEnum.ENABLED) {
            return "NORMAL";
        }
        return status.name();
    }
}
