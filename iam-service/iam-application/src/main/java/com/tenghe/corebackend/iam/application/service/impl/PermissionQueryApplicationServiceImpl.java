package com.tenghe.corebackend.iam.application.service.impl;

import com.tenghe.corebackend.iam.application.PermissionQueryApplicationService;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import com.tenghe.corebackend.iam.interfaces.OrgMembershipRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.PermissionRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.RoleGrantRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.RolePermissionRepositoryPort;
import com.tenghe.corebackend.iam.interfaces.UserRepositoryPort;
import com.tenghe.corebackend.iam.model.Permission;
import com.tenghe.corebackend.iam.model.RoleGrant;
import com.tenghe.corebackend.iam.model.User;
import com.tenghe.corebackend.iam.model.enums.PermissionStatusEnum;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * 权限查询服务实现
 * 供其他微服务调用进行权限校验
 */
@Service
public class PermissionQueryApplicationServiceImpl implements PermissionQueryApplicationService {

    private final UserRepositoryPort userRepository;
    private final OrgMembershipRepositoryPort orgMembershipRepository;
    private final RoleGrantRepositoryPort roleGrantRepository;
    private final RolePermissionRepositoryPort rolePermissionRepository;
    private final PermissionRepositoryPort permissionRepository;

    public PermissionQueryApplicationServiceImpl(
            UserRepositoryPort userRepository,
            OrgMembershipRepositoryPort orgMembershipRepository,
            RoleGrantRepositoryPort roleGrantRepository,
            RolePermissionRepositoryPort rolePermissionRepository,
            PermissionRepositoryPort permissionRepository) {
        this.userRepository = userRepository;
        this.orgMembershipRepository = orgMembershipRepository;
        this.roleGrantRepository = roleGrantRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public Set<String> getUserPermissionCodes(Long userId, Long organizationId) {
        validateUserAndOrg(userId, organizationId);

        List<RoleGrant> grants = roleGrantRepository.listByUserIdAndOrganizationId(userId, organizationId);
        if (grants.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> roleIds = grants.stream()
                .map(RoleGrant::getRoleId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        if (roleIds.isEmpty()) {
            return new HashSet<>();
        }

        Set<Long> permissionIds = new HashSet<>();
        for (Long roleId : roleIds) {
            permissionIds.addAll(rolePermissionRepository.findPermissionIdsByRoleId(roleId));
        }

        if (permissionIds.isEmpty()) {
            return new HashSet<>();
        }

        List<Permission> permissions = permissionRepository.findByIds(permissionIds);
        return permissions.stream()
                .filter(p -> p.getStatus() == PermissionStatusEnum.ENABLED)
                .map(Permission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean hasPermission(Long userId, Long organizationId, String permissionCode) {
        if (permissionCode == null || permissionCode.trim().isEmpty()) {
            return false;
        }
        Set<String> userPermissions = getUserPermissionCodes(userId, organizationId);
        return userPermissions.contains(permissionCode);
    }

    @Override
    public Set<String> checkPermissions(Long userId, Long organizationId, List<String> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return new HashSet<>();
        }
        Set<String> userPermissions = getUserPermissionCodes(userId, organizationId);
        return permissionCodes.stream()
                .filter(userPermissions::contains)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Long> getAccessibleOrganizationIds(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        User user = userRepository.findById(userId);
        if (user == null || user.isDeleted()) {
            return new ArrayList<>();
        }
        return orgMembershipRepository.listOrganizationIdsByUserId(userId);
    }

    @Override
    public List<String> getUserRoleCodes(Long userId, Long organizationId) {
        validateUserAndOrg(userId, organizationId);

        List<RoleGrant> grants = roleGrantRepository.listByUserIdAndOrganizationId(userId, organizationId);
        return grants.stream()
                .map(RoleGrant::getRoleCode)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasRole(Long userId, Long organizationId, String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            return false;
        }
        List<String> userRoles = getUserRoleCodes(userId, organizationId);
        return userRoles.contains(roleCode);
    }

    private void validateUserAndOrg(Long userId, Long organizationId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (organizationId == null) {
            throw new BusinessException("组织ID不能为空");
        }
        User user = userRepository.findById(userId);
        if (user == null || user.isDeleted()) {
            throw new BusinessException("用户不存在");
        }
    }
}
