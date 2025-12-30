package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.ExternalMembershipRepositoryPort;
import com.tenghe.corebackend.model.ExternalMembership;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryExternalMembershipRepository implements ExternalMembershipRepositoryPort {
    private final Map<String, ExternalMembership> store = new ConcurrentHashMap<>();

    @Override
    public void addExternalMembership(Long organizationId, Long userId, Long sourceOrganizationId) {
        String key = key(organizationId, userId);
        ExternalMembership membership = store.get(key);
        if (membership == null) {
            membership = new ExternalMembership();
            membership.setOrganizationId(organizationId);
            membership.setUserId(userId);
            membership.setSourceOrganizationId(sourceOrganizationId);
            membership.setCreatedAt(Instant.now());
            store.put(key, membership);
        }
        membership.setDeleted(false);
        membership.setSourceOrganizationId(sourceOrganizationId);
    }

    @Override
    public boolean exists(Long organizationId, Long userId) {
        ExternalMembership membership = store.get(key(organizationId, userId));
        return membership != null && !membership.isDeleted();
    }

    @Override
    public ExternalMembership findActiveByUserId(Long userId) {
        for (ExternalMembership membership : store.values()) {
            if (!membership.isDeleted() && userId.equals(membership.getUserId())) {
                return membership;
            }
        }
        return null;
    }

    @Override
    public List<ExternalMembership> listByOrganizationId(Long organizationId) {
        List<ExternalMembership> results = new ArrayList<>();
        for (ExternalMembership membership : store.values()) {
            if (!membership.isDeleted() && organizationId.equals(membership.getOrganizationId())) {
                results.add(membership);
            }
        }
        return results;
    }

    @Override
    public void softDeleteByOrganizationId(Long organizationId) {
        for (ExternalMembership membership : store.values()) {
            if (organizationId.equals(membership.getOrganizationId())) {
                membership.setDeleted(true);
            }
        }
    }

    @Override
    public void softDeleteByOrganizationIdAndUserId(Long organizationId, Long userId) {
        ExternalMembership membership = store.get(key(organizationId, userId));
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
