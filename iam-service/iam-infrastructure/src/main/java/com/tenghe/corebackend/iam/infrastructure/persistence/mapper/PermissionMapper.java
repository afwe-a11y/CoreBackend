package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.PermissionPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface PermissionMapper {
  int insert(PermissionPo permission);

  int update(PermissionPo permission);

  PermissionPo findById(@Param("id") Long id);

  PermissionPo findByCode(@Param("code") String code);

  List<PermissionPo> listAll();

  List<PermissionPo> findByIds(@Param("ids") Set<Long> ids);

  List<PermissionPo> findByCodes(@Param("codes") Set<String> codes);

  List<PermissionPo> findByParentCode(@Param("parentCode") String parentCode);

  int updateStatusByIds(@Param("ids") Set<Long> ids,
                        @Param("status") String status);
}
