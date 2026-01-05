package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.ApplicationPermissionPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ApplicationPermissionMapper {
  List<ApplicationPermissionPO> listByAppId(@Param("appId") Long appId);

  int softDeleteByAppIdAndCodesNotIn(@Param("appId") Long appId,
                                     @Param("codes") Set<String> codes);

  int softDeleteByAppId(@Param("appId") Long appId);

  int restoreByAppIdAndCode(@Param("appId") Long appId,
                            @Param("code") String code);

  int insert(ApplicationPermissionPO permission);

  int existsByAppIdAndCode(@Param("appId") Long appId,
                           @Param("code") String code);
}
