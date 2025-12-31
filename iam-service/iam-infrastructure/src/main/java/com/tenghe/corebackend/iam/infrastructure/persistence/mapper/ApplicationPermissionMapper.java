package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.ApplicationPermissionPo;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApplicationPermissionMapper {
    List<ApplicationPermissionPo> listByAppId(@Param("appId") Long appId);

    int softDeleteByAppIdAndCodesNotIn(@Param("appId") Long appId,
                                       @Param("codes") Set<String> codes);

    int softDeleteByAppId(@Param("appId") Long appId);

    int restoreByAppIdAndCode(@Param("appId") Long appId,
                              @Param("code") String code);

    int insert(ApplicationPermissionPo permission);

    int existsByAppIdAndCode(@Param("appId") Long appId,
                             @Param("code") String code);
}
