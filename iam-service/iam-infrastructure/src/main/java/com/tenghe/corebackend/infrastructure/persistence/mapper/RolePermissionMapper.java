package com.tenghe.corebackend.infrastructure.persistence.mapper;

import com.tenghe.corebackend.infrastructure.persistence.po.RolePermissionPo;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RolePermissionMapper {
    List<RolePermissionPo> listByRoleId(@Param("roleId") Long roleId);

    int softDeleteByRoleId(@Param("roleId") Long roleId);

    int softDeleteByRoleIdAndCodesNotIn(@Param("roleId") Long roleId,
                                        @Param("codes") Set<String> codes);

    int restoreByRoleIdAndCode(@Param("roleId") Long roleId,
                               @Param("code") String code);

    int insert(RolePermissionPo permission);

    int existsByRoleId(@Param("roleId") Long roleId);

    int existsByAppIdAndCode(@Param("appId") Long appId,
                             @Param("code") String code);
}
