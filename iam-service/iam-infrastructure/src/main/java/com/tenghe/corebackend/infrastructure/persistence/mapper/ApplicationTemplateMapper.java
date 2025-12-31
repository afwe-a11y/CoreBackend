package com.tenghe.corebackend.infrastructure.persistence.mapper;

import com.tenghe.corebackend.infrastructure.persistence.po.ApplicationTemplatePo;
import java.util.List;
import java.util.Set;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
