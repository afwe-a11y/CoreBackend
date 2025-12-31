package com.tenghe.corebackend.infrastructure.persistence.mapper;

import com.tenghe.corebackend.infrastructure.persistence.po.RolePo;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoleMapper {
    int insert(RolePo role);

    int update(RolePo role);

    RolePo findById(@Param("id") Long id);

    RolePo findByCode(@Param("code") String code);

    RolePo findByNameAndAppId(@Param("name") String name,
                              @Param("appId") Long appId);

    List<RolePo> listAll();

    List<RolePo> listByAppId(@Param("appId") Long appId);

    List<RolePo> findByIds(@Param("ids") Set<Long> ids);

    List<RolePo> findByCodes(@Param("codes") Set<String> codes);

    int countByAppId(@Param("appId") Long appId);

    int softDeleteById(@Param("id") Long id);
}
