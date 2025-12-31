package com.tenghe.corebackend.infrastructure.persistence.mapper;

import com.tenghe.corebackend.infrastructure.persistence.po.UserRolePo;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleGrantMapper {
    int insert(UserRolePo grant);

    List<UserRolePo> listByUserIdAndOrgId(@Param("userId") Long userId,
                                          @Param("orgId") Long orgId);

    List<UserRolePo> listByRoleId(@Param("roleId") Long roleId);

    List<UserRolePo> listByRoleIdAndOrgId(@Param("roleId") Long roleId,
                                          @Param("orgId") Long orgId);

    int softDeleteByOrgIdAndAppIds(@Param("orgId") Long orgId,
                                   @Param("appIds") Set<Long> appIds);

    int softDeleteByOrgId(@Param("orgId") Long orgId);

    int softDeleteByUserIdAndOrgId(@Param("userId") Long userId,
                                   @Param("orgId") Long orgId);

    int softDeleteByUserIdAndOrgIdAndRoleId(@Param("userId") Long userId,
                                            @Param("orgId") Long orgId,
                                            @Param("roleId") Long roleId);

    int updateAssetScopeByUserIdAndOrgId(@Param("userId") Long userId,
                                         @Param("orgId") Long orgId,
                                         @Param("assetScope") String assetScope);

    int existsByRoleId(@Param("roleId") Long roleId);

    int existsByRoleCode(@Param("roleCode") String roleCode);

    int softDeleteByRoleId(@Param("roleId") Long roleId);
}
