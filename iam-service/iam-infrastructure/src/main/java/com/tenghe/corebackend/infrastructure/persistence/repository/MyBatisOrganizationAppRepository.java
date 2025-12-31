package com.tenghe.corebackend.infrastructure.persistence.repository;

import com.tenghe.corebackend.infrastructure.persistence.mapper.ApplicationInstanceMapper;
import com.tenghe.corebackend.infrastructure.persistence.po.ApplicationInstancePo;
import com.tenghe.corebackend.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.interfaces.OrganizationAppRepositoryPort;
import com.tenghe.corebackend.model.OrganizationApp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisOrganizationAppRepository implements OrganizationAppRepositoryPort {
    private static final String STATUS_NORMAL = "NORMAL";

    private final ApplicationInstanceMapper applicationInstanceMapper;
    private final IdGeneratorPort idGenerator;

    public MyBatisOrganizationAppRepository(ApplicationInstanceMapper applicationInstanceMapper,
                                            IdGeneratorPort idGenerator) {
        this.applicationInstanceMapper = applicationInstanceMapper;
        this.idGenerator = idGenerator;
    }

    @Override
    public List<OrganizationApp> listByOrganizationId(Long organizationId) {
        List<ApplicationInstancePo> instances = applicationInstanceMapper.listByOrgId(organizationId);
        return instances.stream()
                .map(instance -> {
                    OrganizationApp app = new OrganizationApp();
                    app.setOrganizationId(instance.getOrgId());
                    app.setAppId(instance.getTemplateId());
                    app.setDeleted(instance.getDeleted() != null && instance.getDeleted() == 1);
                    return app;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Set<Long> findAppIdsByOrganizationIds(Set<Long> organizationIds) {
        if (organizationIds == null || organizationIds.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(applicationInstanceMapper.listTemplateIdsByOrgIds(organizationIds));
    }

    @Override
    public void replaceOrgApps(Long organizationId, Set<Long> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            applicationInstanceMapper.softDeleteByOrgId(organizationId);
            return;
        }
        applicationInstanceMapper.softDeleteByOrgIdAndTemplateIdsNotIn(organizationId, appIds);
        for (Long appId : appIds) {
            int restored = applicationInstanceMapper.restoreByOrgIdAndTemplateId(organizationId, appId);
            if (restored == 0) {
                applicationInstanceMapper.insertFromTemplate(idGenerator.nextId(), organizationId, appId, STATUS_NORMAL);
            }
        }
    }
}
