package com.tenghe.corebackend.iam.interfaces;

import com.tenghe.corebackend.iam.model.Organization;

import java.util.List;

public interface OrganizationRepositoryPort {
  Organization save(Organization organization);

  Organization update(Organization organization);

  Organization findById(Long id);

  Organization findByName(String name);

  Organization findByCode(String code);

  List<Organization> listAll();
}
