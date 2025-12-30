package com.tenghe.corebackend.interfaces;

import com.tenghe.corebackend.model.RoleCategory;
import com.tenghe.corebackend.model.RoleGrant;
import java.util.List;
import java.util.Set;

public interface RoleGrantRepositoryPort {
    void saveAll(List<RoleGrant> grants);

    List<RoleGrant> listByUserIdAndOrganizationId(Long userId, Long organizationId);

    void softDeleteByOrganizationIdAndAppIds(Long organizationId, Set<Long> appIds);

    void softDeleteByOrganizationId(Long organizationId);

    void softDeleteByUserIdAndOrganizationId(Long userId, Long organizationId);

    void updateRoleCategoryByUserAndOrganization(Long userId, Long organizationId, RoleCategory roleCategory);
}
