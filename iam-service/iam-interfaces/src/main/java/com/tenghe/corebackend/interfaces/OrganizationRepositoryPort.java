package com.tenghe.corebackend.interfaces;

import com.tenghe.corebackend.model.Organization;
import java.util.List;

public interface OrganizationRepositoryPort {
    Organization save(Organization organization);

    Organization update(Organization organization);

    Organization findById(Long id);

    Organization findByName(String name);

    Organization findByCode(String code);

    List<Organization> listAll();
}
