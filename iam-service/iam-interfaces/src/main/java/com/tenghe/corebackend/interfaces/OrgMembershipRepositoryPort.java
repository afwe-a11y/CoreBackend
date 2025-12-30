package com.tenghe.corebackend.interfaces;

import java.util.List;

public interface OrgMembershipRepositoryPort {
    void addMembership(Long organizationId, Long userId);

    boolean exists(Long organizationId, Long userId);

    List<Long> listUserIdsByOrganizationId(Long organizationId);

    List<Long> listOrganizationIdsByUserId(Long userId);

    void softDeleteByOrganizationId(Long organizationId);

    void softDeleteByOrganizationIdAndUserId(Long organizationId, Long userId);

    long countByOrganizationId(Long organizationId);
}
