package com.tenghe.corebackend.infrastructure.persistence.repository;

import com.tenghe.corebackend.infrastructure.persistence.json.JsonCodec;
import com.tenghe.corebackend.infrastructure.persistence.mapper.RoleMapper;
import com.tenghe.corebackend.infrastructure.persistence.po.RolePo;
import com.tenghe.corebackend.interfaces.RoleRepositoryPort;
import com.tenghe.corebackend.model.Role;
import com.tenghe.corebackend.model.enums.RoleStatusEnum;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisRoleRepository implements RoleRepositoryPort {
    private static final String ROLE_TYPE_SYSTEM = "SYSTEM";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STATUS = "status";

    private final RoleMapper roleMapper;
    private final JsonCodec jsonCodec;

    public MyBatisRoleRepository(RoleMapper roleMapper, JsonCodec jsonCodec) {
        this.roleMapper = roleMapper;
        this.jsonCodec = jsonCodec;
    }

    @Override
    public Role save(Role role) {
        roleMapper.insert(toPo(role));
        return role;
    }

    @Override
    public Role update(Role role) {
        roleMapper.update(toPo(role));
        return role;
    }

    @Override
    public Role findById(Long id) {
        return toModel(roleMapper.findById(id));
    }

    @Override
    public Role findByCode(String roleCode) {
        return toModel(roleMapper.findByCode(roleCode));
    }

    @Override
    public Role findByNameAndAppId(String roleName, Long appId) {
        return toModel(roleMapper.findByNameAndAppId(roleName, appId));
    }

    @Override
    public List<Role> listAll() {
        return roleMapper.listAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> listByAppId(Long appId) {
        return roleMapper.listByAppId(appId).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return roleMapper.findByIds(ids).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findByCodes(Set<String> roleCodes) {
        if (roleCodes == null || roleCodes.isEmpty()) {
            return List.of();
        }
        return roleMapper.findByCodes(roleCodes).stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public int countByAppId(Long appId) {
        return roleMapper.countByAppId(appId);
    }

    @Override
    public void softDeleteById(Long id) {
        roleMapper.softDeleteById(id);
    }

    private Role toModel(RolePo po) {
        if (po == null) {
            return null;
        }
        Role role = new Role();
        role.setId(po.getId());
        role.setAppId(po.getAppId());
        role.setRoleCode(po.getRoleCode());
        role.setRoleName(po.getRoleName());
        role.setPreset(ROLE_TYPE_SYSTEM.equalsIgnoreCase(po.getRoleType()));
        role.setCreatedAt(po.getCreateTime());
        role.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
        applyDescription(role, po.getDescription());
        return role;
    }

    private RolePo toPo(Role role) {
        RolePo po = new RolePo();
        po.setId(role.getId());
        po.setAppId(role.getAppId());
        po.setRoleCode(role.getRoleCode());
        po.setRoleName(role.getRoleName());
        po.setRoleType(role.isPreset() ? ROLE_TYPE_SYSTEM : "CUSTOM");
        po.setCreateTime(role.getCreatedAt());
        po.setDeleted(role.isDeleted() ? 1 : 0);
        po.setDescription(encodeDescription(role));
        return po;
    }

    private void applyDescription(Role role, String rawDescription) {
        Map<String, Object> payload = jsonCodec.readMap(rawDescription);
        if (payload.isEmpty()) {
            role.setDescription(rawDescription);
            role.setStatus(RoleStatusEnum.ENABLED);
            return;
        }
        role.setDescription(stringValue(payload.get(KEY_DESCRIPTION)));
        role.setStatus(RoleStatusEnum.fromValue(stringValue(payload.get(KEY_STATUS))));
    }

    private String encodeDescription(Role role) {
        boolean hasExtras = role.getStatus() != null || role.getDescription() != null;
        if (!hasExtras) {
            return null;
        }
        Map<String, Object> payload = new LinkedHashMap<>();
        if (role.getDescription() != null) {
            payload.put(KEY_DESCRIPTION, role.getDescription());
        }
        if (role.getStatus() != null) {
            payload.put(KEY_STATUS, role.getStatus().name());
        }
        return jsonCodec.writeValue(payload);
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }
}
