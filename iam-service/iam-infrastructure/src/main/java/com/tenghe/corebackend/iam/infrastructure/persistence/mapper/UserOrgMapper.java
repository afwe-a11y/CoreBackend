package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.UserOrgPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
