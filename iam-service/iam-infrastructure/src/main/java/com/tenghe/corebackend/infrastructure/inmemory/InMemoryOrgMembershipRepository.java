package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.OrgMembershipRepositoryPort;
import com.tenghe.corebackend.model.OrgMembership;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrgMembershipRepository implements OrgMembershipRepositoryPort {
    private final Map<String, OrgMembership> store = new ConcurrentHashMap<>();

    @Override
    public void addMembership(Long organizationId, Long userId) {
        String key = key(organizationId, userId);
        OrgMembership membership = store.get(key);
        if (membership == null) {
            membership = new OrgMembership();
            membership.setOrganizationId(organizationId);
            membership.setUserId(userId);
            membership.setCreatedAt(Instant.now());
            store.put(key, membership);
        }
        membership.setDeleted(false);
    }

    @Override
    public boolean exists(Long organizationId, Long userId) {
        OrgMembership membership = store.get(key(organizationId, userId));
        return membership != null && !membership.isDeleted();
    }

    @Override
    public List<Long> listUserIdsByOrganizationId(Long organizationId) {
        List<Long> results = new ArrayList<>();
        for (OrgMembership membership : store.values()) {
            if (!membership.isDeleted() && organizationId.equals(membership.getOrganizationId())) {
                results.add(membership.getUserId());
            }
        }
        return results;
    }

    @Override
    public List<Long> listOrganizationIdsByUserId(Long userId) {
        List<Long> results = new ArrayList<>();
        for (OrgMembership membership : store.values()) {
            if (!membership.isDeleted() && userId.equals(membership.getUserId())) {
                results.add(membership.getOrganizationId());
            }
        }
        return results;
    }

    @Override
    public void softDeleteByOrganizationId(Long organizationId) {
        for (OrgMembership membership : store.values()) {
            if (organizationId.equals(membership.getOrganizationId())) {
                membership.setDeleted(true);
            }
        }
    }

    @Override
    public void softDeleteByOrganizationIdAndUserId(Long organizationId, Long userId) {
        OrgMembership membership = store.get(key(organizationId, userId));
        if (membership != null) {
            membership.setDeleted(true);
        }
    }

    @Override
    public long countByOrganizationId(Long organizationId) {
        return store.values().stream()
                .filter(membership -> !membership.isDeleted() && organizationId.equals(membership.getOrganizationId()))
                .count();
    }

    private String key(Long organizationId, Long userId) {
        return organizationId + ":" + userId;
    }
}
