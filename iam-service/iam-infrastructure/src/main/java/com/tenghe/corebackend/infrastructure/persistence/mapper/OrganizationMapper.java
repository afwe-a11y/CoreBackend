package com.tenghe.corebackend.infrastructure.persistence.mapper;

import com.tenghe.corebackend.infrastructure.persistence.po.OrganizationPo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrganizationMapper {
    int insert(OrganizationPo organization);

    int update(OrganizationPo organization);

    OrganizationPo findById(@Param("id") Long id);

    OrganizationPo findByName(@Param("name") String name);

    OrganizationPo findByCode(@Param("code") String code);

    List<OrganizationPo> listAll();
}
