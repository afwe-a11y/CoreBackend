package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.RoleGrantRepositoryPort;
import com.tenghe.corebackend.model.enums.RoleCategoryEnum;
import com.tenghe.corebackend.model.RoleGrant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRoleGrantRepository implements RoleGrantRepositoryPort {
    private final Map<Long, RoleGrant> store = new ConcurrentHashMap<>();

    @Override
    public void saveAll(List<RoleGrant> grants) {
        if (grants == null) {
            return;
        }
        for (RoleGrant grant : grants) {
            store.put(grant.getId(), grant);
        }
    }

    @Override
    public List<RoleGrant> listByUserIdAndOrganizationId(Long userId, Long organizationId) {
        List<RoleGrant> results = new ArrayList<>();
        for (RoleGrant grant : store.values()) {
            if (grant.isDeleted()) {
                continue;
            }
            if (userId.equals(grant.getUserId()) && organizationId.equals(grant.getOrganizationId())) {
                results.add(grant);
            }
        }
        return results;
    }

    @Override
    public void softDeleteByOrganizationIdAndAppIds(Long organizationId, Set<Long> appIds) {
        if (appIds == null || appIds.isEmpty()) {
            return;
        }
        for (RoleGrant grant : store.values()) {
            if (grant.isDeleted()) {
                continue;
            }
            if (organizationId.equals(grant.getOrganizationId()) && appIds.contains(grant.getAppId())) {
                grant.setDeleted(true);
            }
        }
    }

    @Override
    public void softDeleteByOrganizationId(Long organizationId) {
        for (RoleGrant grant : store.values()) {
            if (organizationId.equals(grant.getOrganizationId())) {
                grant.setDeleted(true);
            }
        }
    }

    @Override
    public void softDeleteByUserIdAndOrganizationId(Long userId, Long organizationId) {
        for (RoleGrant grant : store.values()) {
            if (organizationId.equals(grant.getOrganizationId()) && userId.equals(grant.getUserId())) {
                grant.setDeleted(true);
            }
        }
    }

    @Override
    public void updateRoleCategoryByUserAndOrganization(Long userId, Long organizationId, RoleCategoryEnum roleCategory) {
        for (RoleGrant grant : store.values()) {
            if (grant.isDeleted()) {
                continue;
            }
            if (organizationId.equals(grant.getOrganizationId()) && userId.equals(grant.getUserId())) {
                grant.setRoleCategory(roleCategory);
            }
        }
    }
}
