package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface RoleMapper {
  int insert(RolePO role);

  int update(RolePO role);

  RolePO findById(@Param("id") Long id);

  RolePO findByCode(@Param("code") String code);

  RolePO findByNameAndAppId(@Param("name") String name,
                            @Param("appId") Long appId);

  List<RolePO> listAll();

  List<RolePO> listByAppId(@Param("appId") Long appId);

  List<RolePO> findByIds(@Param("ids") Set<Long> ids);

  List<RolePO> findByCodes(@Param("codes") Set<String> codes);

  int countByAppId(@Param("appId") Long appId);

  int softDeleteById(@Param("id") Long id);
}
