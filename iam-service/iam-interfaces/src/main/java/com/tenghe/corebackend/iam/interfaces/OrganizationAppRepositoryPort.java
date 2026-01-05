package com.tenghe.corebackend.iam.interfaces;

import com.tenghe.corebackend.iam.model.OrganizationApp;

import java.util.List;
import java.util.Set;

public interface OrganizationAppRepositoryPort {
  List<OrganizationApp> listByOrganizationId(Long organizationId);

  Set<Long> findAppIdsByOrganizationIds(Set<Long> organizationIds);

  void replaceOrgApps(Long organizationId, Set<Long> appIds);
}
