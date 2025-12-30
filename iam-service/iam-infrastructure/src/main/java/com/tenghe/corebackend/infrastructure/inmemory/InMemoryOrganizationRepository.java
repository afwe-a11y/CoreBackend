package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.OrganizationRepositoryPort;
import com.tenghe.corebackend.model.Organization;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryOrganizationRepository implements OrganizationRepositoryPort {
    private final Map<Long, Organization> store = new ConcurrentHashMap<>();

    @Override
    public Organization save(Organization organization) {
        store.put(organization.getId(), organization);
        return organization;
    }

    @Override
    public Organization update(Organization organization) {
        store.put(organization.getId(), organization);
        return organization;
    }

    @Override
    public Organization findById(Long id) {
        return store.get(id);
    }

    @Override
    public Organization findByName(String name) {
        if (name == null) {
            return null;
        }
        for (Organization organization : store.values()) {
            if (name.equals(organization.getName())) {
                return organization;
            }
        }
        return null;
    }

    @Override
    public Organization findByCode(String code) {
        if (code == null) {
            return null;
        }
        for (Organization organization : store.values()) {
            if (code.equals(organization.getCode())) {
                return organization;
            }
        }
        return null;
    }

    @Override
    public List<Organization> listAll() {
        List<Organization> results = new ArrayList<>();
        for (Organization organization : store.values()) {
            if (!organization.isDeleted()) {
                results.add(organization);
            }
        }
        return results;
    }
}
