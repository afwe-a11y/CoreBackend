package com.tenghe.corebackend.iam.infrastructure.persistence.repository;

import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.PermissionMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.RoleMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.mapper.RolePermissionMapper;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.PermissionPo;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.RolePermissionPo;
import com.tenghe.corebackend.iam.infrastructure.persistence.po.RolePo;
import com.tenghe.corebackend.iam.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.iam.interfaces.RolePermissionRepositoryPort;
import com.tenghe.corebackend.iam.model.RolePermission;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MyBatisRolePermissionRepository implements RolePermissionRepositoryPort {
    private final RolePermissionMapper rolePermissionMapper;
    private final PermissionMapper permissionMapper;
    private final RoleMapper roleMapper;
    private final IdGeneratorPort idGenerator;

    public MyBatisRolePermissionRepository(RolePermissionMapper rolePermissionMapper,
                                           PermissionMapper permissionMapper,
                                           RoleMapper roleMapper,
                                           IdGeneratorPort idGenerator) {
        this.rolePermissionMapper = rolePermissionMapper;
        this.permissionMapper = permissionMapper;
        this.roleMapper = roleMapper;
        this.idGenerator = idGenerator;
    }

    @Override
    public void saveAll(List<RolePermission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return;
        }
        Set<Long> permissionIds = permissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());
        Map<Long, String> codes = mapPermissionIdsToCodes(permissionIds);
        RolePo role = roleMapper.findById(permissions.get(0).getRoleId());
        Long appId = role == null ? null : role.getAppId();
        if (appId == null) {
            return;
        }
        for (RolePermission permission : permissions) {
            String code = codes.get(permission.getPermissionId());
            if (code == null) {
                continue;
            }
            RolePermissionPo po = new RolePermissionPo();
            po.setId(idGenerator.nextId());
            po.setAppId(appId);
            po.setRoleId(permission.getRoleId());
            po.setPermCode(code);
            po.setCreateTime(Instant.now());
            po.setDeleted(0);
            rolePermissionMapper.insert(po);
        }
    }

    @Override
    public List<RolePermission> listByRoleId(Long roleId) {
        List<RolePermissionPo> permissions = rolePermissionMapper.listByRoleId(roleId);
        if (permissions.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> codes = permissions.stream()
                .map(RolePermissionPo::getPermCode)
                .collect(Collectors.toSet());
        Map<String, Long> ids = mapPermissionCodesToIds(codes);
        return permissions.stream()
                .map(po -> toModel(po, ids.get(po.getPermCode())))
                .filter(permission -> permission.getPermissionId() != null)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Long> findPermissionIdsByRoleId(Long roleId) {
        List<RolePermissionPo> permissions = rolePermissionMapper.listByRoleId(roleId);
        if (permissions.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> codes = permissions.stream()
                .map(RolePermissionPo::getPermCode)
                .collect(Collectors.toSet());
        return new HashSet<>(mapPermissionCodesToIds(codes).values());
    }

    @Override
    public void replaceRolePermissions(Long roleId, Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            rolePermissionMapper.softDeleteByRoleId(roleId);
            return;
        }
        Map<Long, String> codes = mapPermissionIdsToCodes(permissionIds);
        Set<String> codeSet = new HashSet<>(codes.values());
        if (codeSet.isEmpty()) {
            rolePermissionMapper.softDeleteByRoleId(roleId);
            return;
        }
        rolePermissionMapper.softDeleteByRoleIdAndCodesNotIn(roleId, codeSet);
        RolePo role = roleMapper.findById(roleId);
        Long appId = role == null ? null : role.getAppId();
        if (appId == null) {
            return;
        }
        for (String code : codeSet) {
            int restored = rolePermissionMapper.restoreByRoleIdAndCode(roleId, code);
            if (restored == 0) {
                RolePermissionPo po = new RolePermissionPo();
                po.setId(idGenerator.nextId());
                po.setAppId(appId);
                po.setRoleId(roleId);
                po.setPermCode(code);
                po.setCreateTime(Instant.now());
                po.setDeleted(0);
                rolePermissionMapper.insert(po);
            }
        }
    }

    @Override
    public void softDeleteByRoleId(Long roleId) {
        rolePermissionMapper.softDeleteByRoleId(roleId);
    }

    @Override
    public boolean existsByPermissionIdAndAppId(Long permissionId, Long appId) {
        String code = mapPermissionIdsToCodes(Collections.singleton(permissionId)).get(permissionId);
        if (code == null) {
            return false;
        }
        return rolePermissionMapper.existsByAppIdAndCode(appId, code) > 0;
    }

    @Override
    public boolean existsByRoleId(Long roleId) {
        return rolePermissionMapper.existsByRoleId(roleId) > 0;
    }

    private RolePermission toModel(RolePermissionPo po, Long permissionId) {
        RolePermission permission = new RolePermission();
        permission.setId(po.getId());
        permission.setRoleId(po.getRoleId());
        permission.setPermissionId(permissionId);
        permission.setCreatedAt(po.getCreateTime());
        permission.setDeleted(po.getDeleted() != null && po.getDeleted() == 1);
        return permission;
    }

    private Map<Long, String> mapPermissionIdsToCodes(Set<Long> permissionIds) {
        if (permissionIds == null || permissionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<PermissionPo> permissions = permissionMapper.findByIds(permissionIds);
        Map<Long, String> map = new HashMap<>();
        for (PermissionPo permission : permissions) {
            map.put(permission.getId(), permission.getPermCode());
        }
        return map;
    }

    private Map<String, Long> mapPermissionCodesToIds(Set<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<PermissionPo> permissions = permissionMapper.findByCodes(codes);
        Map<String, Long> map = new HashMap<>();
        for (PermissionPo permission : permissions) {
            map.put(permission.getPermCode(), permission.getId());
        }
        return map;
    }
}
