package com.tenghe.corebackend.infrastructure.persistence.mapper;

import com.tenghe.corebackend.infrastructure.persistence.po.UserOrgPo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserOrgMapper {
    int insert(UserOrgPo membership);

    int exists(@Param("orgId") Long orgId,
               @Param("userId") Long userId,
               @Param("identityType") String identityType);

    List<Long> listUserIdsByOrgId(@Param("orgId") Long orgId,
                                  @Param("identityType") String identityType);

    List<Long> listOrgIdsByUserId(@Param("userId") Long userId,
                                  @Param("identityType") String identityType);

    int softDeleteByOrgId(@Param("orgId") Long orgId,
                          @Param("identityType") String identityType);

    int softDeleteByOrgIdAndUserId(@Param("orgId") Long orgId,
                                   @Param("userId") Long userId,
                                   @Param("identityType") String identityType);

    long countByOrgId(@Param("orgId") Long orgId,
                      @Param("identityType") String identityType);

    List<UserOrgPo> listExternalByOrgId(@Param("orgId") Long orgId);

    UserOrgPo findExternalByUserId(@Param("userId") Long userId);
}
