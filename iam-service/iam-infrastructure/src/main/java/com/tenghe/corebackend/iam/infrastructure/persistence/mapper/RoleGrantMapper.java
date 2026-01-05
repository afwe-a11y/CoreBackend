package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.UserRolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface RoleGrantMapper {
  int insert(UserRolePO grant);

  List<UserRolePO> listByUserIdAndOrgId(@Param("userId") Long userId,
                                        @Param("orgId") Long orgId);

  List<UserRolePO> listByRoleId(@Param("roleId") Long roleId);

  List<UserRolePO> listByRoleIdAndOrgId(@Param("roleId") Long roleId,
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
