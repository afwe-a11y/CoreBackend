package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.ApplicationInstancePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ApplicationInstanceMapper {
  List<ApplicationInstancePO> listByOrgId(@Param("orgId") Long orgId);

  List<Long> listTemplateIdsByOrgIds(@Param("orgIds") Set<Long> orgIds);

  int softDeleteByOrgIdAndTemplateIdsNotIn(@Param("orgId") Long orgId,
                                           @Param("templateIds") Set<Long> templateIds);

  int softDeleteByOrgId(@Param("orgId") Long orgId);

  int restoreByOrgIdAndTemplateId(@Param("orgId") Long orgId,
                                  @Param("templateId") Long templateId);

  ApplicationInstancePO findByOrgIdAndTemplateId(@Param("orgId") Long orgId,
                                                 @Param("templateId") Long templateId);

  int insertFromTemplate(@Param("id") Long id,
                         @Param("orgId") Long orgId,
                         @Param("templateId") Long templateId,
                         @Param("status") String status);
}
