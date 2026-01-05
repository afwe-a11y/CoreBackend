package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.PermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface PermissionMapper {
  int insert(PermissionPO permission);

  int update(PermissionPO permission);

  PermissionPO findById(@Param("id") Long id);

  PermissionPO findByCode(@Param("code") String code);

  List<PermissionPO> listAll();

  List<PermissionPO> findByIds(@Param("ids") Set<Long> ids);

  List<PermissionPO> findByCodes(@Param("codes") Set<String> codes);

  List<PermissionPO> findByParentCode(@Param("parentCode") String parentCode);

  int updateStatusByIds(@Param("ids") Set<Long> ids,
                        @Param("status") String status);
}
