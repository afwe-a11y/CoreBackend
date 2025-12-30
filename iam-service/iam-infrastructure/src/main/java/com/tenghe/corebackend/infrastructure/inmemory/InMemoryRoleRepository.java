package com.tenghe.corebackend.infrastructure.inmemory;

import com.tenghe.corebackend.interfaces.RoleRepositoryPort;
import com.tenghe.corebackend.model.Role;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryRoleRepository implements RoleRepositoryPort {
    private final Map<Long, Role> store = new ConcurrentHashMap<>();

    @Override
    public Role save(Role role) {
        store.put(role.getId(), role);
        return role;
    }

    @Override
    public Role update(Role role) {
        store.put(role.getId(), role);
        return role;
    }

    @Override
    public Role findById(Long id) {
        if (id == null) {
            return null;
        }
        Role role = store.get(id);
        if (role != null && role.isDeleted()) {
            return null;
        }
        return role;
    }

    @Override
    public Role findByCode(String roleCode) {
        if (roleCode == null) {
            return null;
        }
        for (Role role : store.values()) {
            if (!role.isDeleted() && roleCode.equals(role.getRoleCode())) {
                return role;
            }
        }
        return null;
    }

    @Override
    public Role findByNameAndAppId(String roleName, Long appId) {
        if (roleName == null || appId == null) {
            return null;
        }
        for (Role role : store.values()) {
            if (!role.isDeleted() && roleName.equals(role.getRoleName()) && appId.equals(role.getAppId())) {
                return role;
            }
        }
        return null;
    }

    @Override
    public List<Role> listAll() {
        List<Role> results = new ArrayList<>();
        for (Role role : store.values()) {
            if (!role.isDeleted()) {
                results.add(role);
            }
        }
        return results;
    }

    @Override
    public List<Role> listByAppId(Long appId) {
        List<Role> results = new ArrayList<>();
        for (Role role : store.values()) {
            if (!role.isDeleted() && appId.equals(role.getAppId())) {
                results.add(role);
            }
        }
        return results;
    }

    @Override
    public List<Role> findByIds(Set<Long> ids) {
        List<Role> results = new ArrayList<>();
        if (ids == null) {
            return results;
        }
        for (Long id : ids) {
            Role role = store.get(id);
            if (role != null && !role.isDeleted()) {
                results.add(role);
            }
        }
        return results;
    }

    @Override
    public List<Role> findByCodes(Set<String> roleCodes) {
        List<Role> results = new ArrayList<>();
        if (roleCodes == null) {
            return results;
        }
        for (Role role : store.values()) {
            if (!role.isDeleted() && roleCodes.contains(role.getRoleCode())) {
                results.add(role);
            }
        }
        return results;
    }

    @Override
    public int countByAppId(Long appId) {
        int count = 0;
        for (Role role : store.values()) {
            if (!role.isDeleted() && appId.equals(role.getAppId())) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void softDeleteById(Long id) {
        Role role = store.get(id);
        if (role != null) {
            role.setDeleted(true);
        }
    }
}
