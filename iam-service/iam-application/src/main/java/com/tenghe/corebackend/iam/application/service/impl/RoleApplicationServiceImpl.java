package com.tenghe.corebackend.iam.application.service.impl;

import com.tenghe.corebackend.iam.application.RoleApplicationService;
import com.tenghe.corebackend.iam.application.command.BatchRoleMemberCommand;
import com.tenghe.corebackend.iam.application.command.ConfigureRolePermissionsCommand;
import com.tenghe.corebackend.iam.application.command.CreateRoleCommand;
import com.tenghe.corebackend.iam.application.command.UpdateRoleCommand;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import com.tenghe.corebackend.iam.application.service.result.RoleDetailResult;
import com.tenghe.corebackend.iam.application.service.result.RoleListItemResult;
import com.tenghe.corebackend.iam.application.service.result.RoleMemberResult;
import com.tenghe.corebackend.iam.application.validation.ValidationUtils;
import com.tenghe.corebackend.iam.interfaces.*;
import com.tenghe.corebackend.iam.model.Application;
import com.tenghe.corebackend.iam.model.Role;
import com.tenghe.corebackend.iam.model.RoleGrant;
import com.tenghe.corebackend.iam.model.User;
import com.tenghe.corebackend.iam.model.enums.RoleCategoryEnum;
import com.tenghe.corebackend.iam.model.enums.RoleStatusEnum;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class RoleApplicationServiceImpl implements RoleApplicationService {
  private static final int DEFAULT_PAGE_SIZE = 10;

  private final RoleRepositoryPort roleRepository;
  private final RolePermissionRepositoryPort rolePermissionRepository;
  private final ApplicationRepositoryPort applicationRepository;
  private final ApplicationPermissionRepositoryPort applicationPermissionRepository;
  private final RoleGrantRepositoryPort roleGrantRepository;
  private final OrgMembershipRepositoryPort orgMembershipRepository;
  private final OrganizationAppRepositoryPort organizationAppRepository;
  private final UserRepositoryPort userRepository;
  private final IdGeneratorPort idGenerator;
  private final TransactionManagerPort transactionManager;

  public RoleApplicationServiceImpl(
      RoleRepositoryPort roleRepository,
      RolePermissionRepositoryPort rolePermissionRepository,
      ApplicationRepositoryPort applicationRepository,
      ApplicationPermissionRepositoryPort applicationPermissionRepository,
      RoleGrantRepositoryPort roleGrantRepository,
      OrgMembershipRepositoryPort orgMembershipRepository,
      OrganizationAppRepositoryPort organizationAppRepository,
      UserRepositoryPort userRepository,
      IdGeneratorPort idGenerator,
      TransactionManagerPort transactionManager) {
    this.roleRepository = roleRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.applicationRepository = applicationRepository;
    this.applicationPermissionRepository = applicationPermissionRepository;
    this.roleGrantRepository = roleGrantRepository;
    this.orgMembershipRepository = orgMembershipRepository;
    this.organizationAppRepository = organizationAppRepository;
    this.userRepository = userRepository;
    this.idGenerator = idGenerator;
    this.transactionManager = transactionManager;
  }

  @Override
  public PageResult<RoleListItemResult> listRoles(Long appId, String keyword, Integer page, Integer size) {
    int pageNumber = normalizePage(page);
    int pageSize = normalizeSize(size);
    List<Role> roles;
    if (appId != null) {
      roles = new ArrayList<>(roleRepository.listByAppId(appId));
    } else {
      roles = new ArrayList<>(roleRepository.listAll());
    }
    if (keyword != null && !keyword.trim().isEmpty()) {
      String keywordValue = keyword.trim();
      roles = roles.stream()
          .filter(role -> matchesKeyword(role, keywordValue))
          .toList();
    }
    roles.sort(Comparator.comparing(Role::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .reversed());
    long total = roles.size();
    List<Role> paged = paginate(roles, pageNumber, pageSize);
    List<RoleListItemResult> items = paged.stream()
        .map(this::toListItem)
        .toList();
    return new PageResult<>(items, total, pageNumber, pageSize);
  }

  @Override
  public Long createRole(CreateRoleCommand command) {
    ValidationUtils.requireNonBlank(command.getRoleName(), "角色名称不能为空");
    ValidationUtils.requireNonBlank(command.getRoleCode(), "角色编码不能为空");
    if (command.getAppId() == null) {
      throw new BusinessException("所属应用不能为空");
    }

    Application application = applicationRepository.findById(command.getAppId());
    if (application == null) {
      throw new BusinessException("所属应用不存在");
    }

    if (roleRepository.findByCode(command.getRoleCode()) != null) {
      throw new BusinessException("角色编码已被占用");
    }
    if (roleRepository.findByNameAndAppId(command.getRoleName(), command.getAppId()) != null) {
      throw new BusinessException("该应用下角色名称已存在");
    }

    Long id = idGenerator.nextId();
    Role role = new Role();
    role.setId(id);
    role.setAppId(command.getAppId());
    role.setRoleName(command.getRoleName());
    role.setRoleCode(command.getRoleCode());
    role.setDescription(command.getDescription());
    role.setStatus(RoleStatusEnum.ENABLED);
    role.setPreset(false);
    role.setCreatedAt(Instant.now());
    role.setDeleted(false);
    roleRepository.save(role);
    return id;
  }

  @Override
  public RoleDetailResult getRoleDetail(Long roleId) {
    Role role = requireRole(roleId);
    Application application = applicationRepository.findById(role.getAppId());
    Set<Long> permissionIds = rolePermissionRepository.findPermissionIdsByRoleId(roleId);
    RoleDetailResult result = new RoleDetailResult();
    result.setId(role.getId());
    result.setAppId(role.getAppId());
    result.setAppName(application == null ? null : application.getAppName());
    result.setRoleName(role.getRoleName());
    result.setRoleCode(role.getRoleCode());
    result.setDescription(role.getDescription());
    result.setStatus(role.getStatus() == null ? null : role.getStatus().name());
    result.setPreset(role.isPreset());
    result.setCreatedAt(role.getCreatedAt());
    result.setPermissionIds(new ArrayList<>(permissionIds));
    return result;
  }

  @Override
  public void updateRole(UpdateRoleCommand command) {
    Role role = requireRole(command.getRoleId());
    if (role.isPreset()) {
      throw new BusinessException("预置角色不可编辑");
    }
    ValidationUtils.requireNonBlank(command.getRoleName(), "角色名称不能为空");

    Role existingByName = roleRepository.findByNameAndAppId(command.getRoleName(), role.getAppId());
    if (existingByName != null && !existingByName.getId().equals(role.getId())) {
      throw new BusinessException("该应用下角色名称已存在");
    }

    RoleStatusEnum status = RoleStatusEnum.fromValue(command.getStatus());
    if (status == null) {
      throw new BusinessException("角色状态不能为空");
    }

    role.setRoleName(command.getRoleName());
    role.setDescription(command.getDescription());
    role.setStatus(status);
    roleRepository.update(role);
  }

  @Override
  public void configureRolePermissions(ConfigureRolePermissionsCommand command) {
    Role role = requireRole(command.getRoleId());
    Set<Long> appPermissionIds = applicationPermissionRepository.findPermissionIdsByAppId(role.getAppId());
    if (command.getPermissionIds() != null) {
      for (Long permissionId : command.getPermissionIds()) {
        if (!appPermissionIds.contains(permissionId)) {
          throw new BusinessException("权限不在应用包含权限范围内");
        }
      }
    }
    Set<Long> permissionIds = command.getPermissionIds() == null ? Collections.emptySet() : new HashSet<>(command.getPermissionIds());
    rolePermissionRepository.replaceRolePermissions(command.getRoleId(), permissionIds);
  }

  @Override
  public void deleteRole(Long roleId) {
    Role role = requireRole(roleId);
    if (role.isPreset()) {
      throw new BusinessException("预置角色不可删除");
    }
    if (hasRoleMembers(roleId)) {
      throw new BusinessException("角色下存在成员，无法删除");
    }
    transactionManager.doInTransaction(() -> {
      role.setDeleted(true);
      roleRepository.update(role);
      rolePermissionRepository.softDeleteByRoleId(roleId);
    });
  }

  @Override
  public PageResult<RoleMemberResult> listRoleMembers(Long roleId, Long organizationId, Integer page, Integer size) {
    requireRole(roleId);
    int pageNumber = normalizePage(page);
    int pageSize = normalizeSize(size);
    List<RoleGrant> grants = organizationId == null
        ? roleGrantRepository.listByRoleId(roleId)
        : roleGrantRepository.listByRoleIdAndOrganizationId(roleId, organizationId);
    long total = grants.size();
    List<RoleGrant> paged = paginate(grants, pageNumber, pageSize);
    List<RoleMemberResult> items = new ArrayList<>();
    for (RoleGrant grant : paged) {
      User user = userRepository.findById(grant.getUserId());
      if (user != null && !user.isDeleted()) {
        RoleMemberResult item = new RoleMemberResult();
        item.setUserId(user.getId());
        item.setUsername(user.getUsername());
        item.setName(user.getName());
        item.setPhone(user.getPhone());
        item.setEmail(user.getEmail());
        items.add(item);
      }
    }
    return new PageResult<>(items, total, pageNumber, pageSize);
  }

  @Override
  public void batchAddMembers(BatchRoleMemberCommand command) {
    Role role = requireRole(command.getRoleId());
    if (command.getOrganizationId() == null) {
      throw new BusinessException("组织不能为空");
    }
    if (command.getUserIds() == null || command.getUserIds().isEmpty()) {
      return;
    }
    if (!organizationAppRepository.findAppIdsByOrganizationIds(Collections.singleton(command.getOrganizationId()))
        .contains(role.getAppId())) {
      throw new BusinessException("组织未开通应用");
    }
    List<RoleGrant> grants = new ArrayList<>();
    for (Long userId : command.getUserIds()) {
      User user = userRepository.findById(userId);
      if (user == null || user.isDeleted()) {
        continue;
      }
      if (!orgMembershipRepository.exists(command.getOrganizationId(), userId)) {
        throw new BusinessException("成员不属于该组织");
      }
      boolean alreadyGranted = roleGrantRepository.listByUserIdAndOrganizationId(userId, command.getOrganizationId())
          .stream()
          .anyMatch(existing -> role.getRoleCode().equals(existing.getRoleCode()));
      if (alreadyGranted) {
        continue;
      }
      RoleGrant grant = new RoleGrant();
      grant.setId(idGenerator.nextId());
      grant.setOrganizationId(command.getOrganizationId());
      grant.setUserId(userId);
      grant.setAppId(role.getAppId());
      grant.setRoleId(role.getId());
      grant.setRoleCode(role.getRoleCode());
      grant.setRoleCategory(RoleCategoryEnum.APPLICATION);
      grant.setCreatedAt(Instant.now());
      grant.setDeleted(false);
      grants.add(grant);
    }
    if (!grants.isEmpty()) {
      roleGrantRepository.saveAll(grants);
    }
  }

  @Override
  public void batchRemoveMembers(BatchRoleMemberCommand command) {
    Role role = requireRole(command.getRoleId());
    if (command.getOrganizationId() == null || command.getUserIds() == null) {
      return;
    }
    for (Long userId : command.getUserIds()) {
      roleGrantRepository.softDeleteByUserIdAndOrganizationIdAndRoleCode(
          userId,
          command.getOrganizationId(),
          role.getRoleCode());
    }
  }

  private Role requireRole(Long roleId) {
    if (roleId == null) {
      throw new BusinessException("角色不存在");
    }
    Role role = roleRepository.findById(roleId);
    if (role == null) {
      throw new BusinessException("角色不存在");
    }
    return role;
  }

  private boolean hasRoleMembers(Long roleId) {
    Role role = roleRepository.findById(roleId);
    if (role == null) {
      return false;
    }
    return roleGrantRepository.existsByRoleId(roleId) || roleGrantRepository.existsByRoleCode(role.getRoleCode());
  }

  private boolean matchesKeyword(Role role, String keyword) {
    if (role.getRoleName() != null && role.getRoleName().contains(keyword)) {
      return true;
    }
    if (role.getRoleCode() != null && role.getRoleCode().contains(keyword)) {
      return true;
    }
    return false;
  }

  private RoleListItemResult toListItem(Role role) {
    Application application = applicationRepository.findById(role.getAppId());
    RoleListItemResult item = new RoleListItemResult();
    item.setId(role.getId());
    item.setAppId(role.getAppId());
    item.setAppName(application == null ? null : application.getAppName());
    item.setRoleName(role.getRoleName());
    item.setRoleCode(role.getRoleCode());
    item.setDescription(role.getDescription());
    item.setStatus(role.getStatus() == null ? null : role.getStatus().name());
    item.setPreset(role.isPreset());
    item.setMemberCount(roleGrantRepository.listByRoleId(role.getId()).size());
    item.setCreatedAt(role.getCreatedAt());
    return item;
  }

  private <T> List<T> paginate(List<T> items, int page, int size) {
    if (items.isEmpty()) {
      return Collections.emptyList();
    }
    int fromIndex = Math.max(0, (page - 1) * size);
    if (fromIndex >= items.size()) {
      return Collections.emptyList();
    }
    int toIndex = Math.min(fromIndex + size, items.size());
    return items.subList(fromIndex, toIndex);
  }

  private int normalizePage(Integer page) {
    if (page == null || page < 1) {
      return 1;
    }
    return page;
  }

  private int normalizeSize(Integer size) {
    if (size == null || size < 1) {
      return DEFAULT_PAGE_SIZE;
    }
    return size;
  }
}
