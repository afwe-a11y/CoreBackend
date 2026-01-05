package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.ApplicationTemplatePo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

@Mapper
public interface ApplicationTemplateMapper {
  int insert(ApplicationTemplatePo application);

  int update(ApplicationTemplatePo application);

  ApplicationTemplatePo findById(@Param("id") Long id);

  ApplicationTemplatePo findByCode(@Param("code") String code);

  List<ApplicationTemplatePo> listAll();

  List<ApplicationTemplatePo> findByIds(@Param("ids") Set<Long> ids);

  int softDeleteById(@Param("id") Long id);
}
