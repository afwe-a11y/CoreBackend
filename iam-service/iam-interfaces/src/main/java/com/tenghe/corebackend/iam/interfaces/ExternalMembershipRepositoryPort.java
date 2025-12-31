package com.tenghe.corebackend.iam.interfaces;

import com.tenghe.corebackend.iam.model.ExternalMembership;
import java.util.List;

public interface ExternalMembershipRepositoryPort {
    void addExternalMembership(Long organizationId, Long userId, Long sourceOrganizationId);

    boolean exists(Long organizationId, Long userId);

    ExternalMembership findActiveByUserId(Long userId);

    List<ExternalMembership> listByOrganizationId(Long organizationId);

    void softDeleteByOrganizationId(Long organizationId);

    void softDeleteByOrganizationIdAndUserId(Long organizationId, Long userId);

    long countByOrganizationId(Long organizationId);
}
