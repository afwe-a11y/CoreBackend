package com.tenghe.corebackend.iam.infrastructure.persistence.mapper;

import com.tenghe.corebackend.iam.infrastructure.persistence.po.OrganizationPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrganizationMapper {
  int insert(OrganizationPO organization);

  int update(OrganizationPO organization);

  OrganizationPO findById(@Param("id") Long id);

  OrganizationPO findByName(@Param("name") String name);

  OrganizationPO findByCode(@Param("code") String code);

  List<OrganizationPO> listAll();
}
