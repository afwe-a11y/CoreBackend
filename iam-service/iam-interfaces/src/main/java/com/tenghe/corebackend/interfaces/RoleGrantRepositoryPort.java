package com.tenghe.corebackend.interfaces;

import com.tenghe.corebackend.model.enums.RoleCategoryEnum;
import com.tenghe.corebackend.model.RoleGrant;
import java.util.List;
import java.util.Set;

public interface RoleGrantRepositoryPort {
    void saveAll(List<RoleGrant> grants);

    List<RoleGrant> listByUserIdAndOrganizationId(Long userId, Long organizationId);

    void softDeleteByOrganizationIdAndAppIds(Long organizationId, Set<Long> appIds);

    void softDeleteByOrganizationId(Long organizationId);

    void softDeleteByUserIdAndOrganizationId(Long userId, Long organizationId);

    void updateRoleCategoryByUserAndOrganization(Long userId, Long organizationId, RoleCategoryEnum roleCategory);

    /**
     * 检查角色是否有成员（通过角色ID）
     */
    boolean existsByRoleId(Long roleId);

    /**
     * 检查角色是否有成员（通过角色码）
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * 根据角色ID查询授权列表
     */
    List<RoleGrant> listByRoleId(Long roleId);

    /**
     * 根据角色ID和组织ID查询授权列表
     */
    List<RoleGrant> listByRoleIdAndOrganizationId(Long roleId, Long organizationId);

    /**
     * 根据角色ID软删除授权
     */
    void softDeleteByRoleId(Long roleId);
}
