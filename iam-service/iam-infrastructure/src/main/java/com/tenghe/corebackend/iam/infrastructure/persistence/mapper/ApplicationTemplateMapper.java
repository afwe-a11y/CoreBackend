package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.ApplicationTemplatePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ApplicationTemplateMapper {
  int insert(ApplicationTemplatePO application);

  int update(ApplicationTemplatePO application);

  ApplicationTemplatePO findById(@Param("id") Long id);

  ApplicationTemplatePO findByCode(@Param("code") String code);

  List<ApplicationTemplatePO> listAll();

  List<ApplicationTemplatePO> findByIds(@Param("ids") Set<Long> ids);

  int softDeleteById(@Param("id") Long id);
}
