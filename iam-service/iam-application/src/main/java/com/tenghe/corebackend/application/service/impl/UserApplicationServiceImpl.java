package com.tenghe.corebackend.application.service.impl;

import com.tenghe.corebackend.application.UserApplicationService;
import com.tenghe.corebackend.application.command.CreateUserCommand;
import com.tenghe.corebackend.application.command.RoleSelectionCommand;
import com.tenghe.corebackend.application.command.UpdateUserCommand;
import com.tenghe.corebackend.application.command.UserQueryCommand;
import com.tenghe.corebackend.application.exception.BusinessException;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.RoleSelectionResult;
import com.tenghe.corebackend.application.service.result.UserDetailResult;
import com.tenghe.corebackend.application.service.result.UserListItemResult;
import com.tenghe.corebackend.application.validation.ValidationUtils;
import com.tenghe.corebackend.interfaces.EmailServicePort;
import com.tenghe.corebackend.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.interfaces.OrgMembershipRepositoryPort;
import com.tenghe.corebackend.interfaces.OrganizationAppRepositoryPort;
import com.tenghe.corebackend.interfaces.OrganizationRepositoryPort;
import com.tenghe.corebackend.interfaces.PasswordEncoderPort;
import com.tenghe.corebackend.interfaces.RoleGrantRepositoryPort;
import com.tenghe.corebackend.interfaces.RoleRepositoryPort;
import com.tenghe.corebackend.interfaces.TransactionManagerPort;
import com.tenghe.corebackend.interfaces.UserRepositoryPort;
import com.tenghe.corebackend.model.Organization;
import com.tenghe.corebackend.model.Role;
import com.tenghe.corebackend.model.enums.RoleCategoryEnum;
import com.tenghe.corebackend.model.RoleGrant;
import com.tenghe.corebackend.model.User;
import com.tenghe.corebackend.model.enums.UserStatusEnum;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class UserApplicationServiceImpl implements UserApplicationService {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final UserRepositoryPort userRepository;
    private final OrganizationRepositoryPort organizationRepository;
    private final OrganizationAppRepositoryPort organizationAppRepository;
    private final OrgMembershipRepositoryPort orgMembershipRepository;
    private final RoleGrantRepositoryPort roleGrantRepository;
    private final RoleRepositoryPort roleRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final EmailServicePort emailService;
    private final IdGeneratorPort idGenerator;
    private final TransactionManagerPort transactionManager;

    public UserApplicationServiceImpl(
            UserRepositoryPort userRepository,
            OrganizationRepositoryPort organizationRepository,
            OrganizationAppRepositoryPort organizationAppRepository,
            OrgMembershipRepositoryPort orgMembershipRepository,
            RoleGrantRepositoryPort roleGrantRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder,
            EmailServicePort emailService,
            IdGeneratorPort idGenerator,
            TransactionManagerPort transactionManager) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.organizationAppRepository = organizationAppRepository;
        this.orgMembershipRepository = orgMembershipRepository;
        this.roleGrantRepository = roleGrantRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.idGenerator = idGenerator;
        this.transactionManager = transactionManager;
    }

    @Override
    public PageResult<UserListItemResult> listUsers(UserQueryCommand query) {
        int pageNumber = normalizePage(query.getPage());
        int pageSize = normalizeSize(query.getSize());

        List<User> users = userRepository.searchByKeyword(query.getKeyword());
        
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            UserStatusEnum status = UserStatusEnum.fromValue(query.getStatus());
            if (status != null) {
                users = users.stream()
                        .filter(u -> u.getStatus() == status)
                        .toList();
            }
        }

        if (query.getOrganizationIds() != null && !query.getOrganizationIds().isEmpty()) {
            Set<Long> targetOrgIds = new HashSet<>(query.getOrganizationIds());
            users = users.stream()
                    .filter(u -> {
                        List<Long> userOrgIds = orgMembershipRepository.listOrganizationIdsByUserId(u.getId());
                        return userOrgIds.stream().anyMatch(targetOrgIds::contains);
                    })
                    .toList();
        }

        if (query.getRoleCodes() != null && !query.getRoleCodes().isEmpty()) {
            Set<String> targetRoleCodes = new HashSet<>(query.getRoleCodes());
            users = users.stream()
                    .filter(u -> {
                        List<Long> userOrgIds = orgMembershipRepository.listOrganizationIdsByUserId(u.getId());
                        for (Long orgId : userOrgIds) {
                            List<RoleGrant> grants = roleGrantRepository.listByUserIdAndOrganizationId(u.getId(), orgId);
                            if (grants.stream().anyMatch(g -> targetRoleCodes.contains(g.getRoleCode()))) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .toList();
        }

        users.sort(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        long total = users.size();
        List<User> paged = paginate(users, pageNumber, pageSize);
        List<UserListItemResult> items = paged.stream()
                .map(this::toListItem)
                .toList();
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    @Override
    public Long createUser(CreateUserCommand command) {
        ValidationUtils.requireNonBlank(command.getUsername(), "用户名不能为空");
        ValidationUtils.validateUsername(command.getUsername(), "用户名格式不正确");
        ValidationUtils.requireNonBlank(command.getEmail(), "邮箱不能为空");
        ValidationUtils.requireEmailFormat(command.getEmail(), "邮箱格式不正确");
        ValidationUtils.requirePhoneFormat(command.getPhone(), "手机号格式不正确");
        ValidationUtils.requireListNotEmpty(command.getOrganizationIds(), "关联组织不能为空");
        ValidationUtils.requireListNotEmpty(command.getRoleSelections(), "关联角色不能为空");

        if (userRepository.findByUsername(command.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        if (userRepository.findByEmail(command.getEmail()) != null) {
            throw new BusinessException("邮箱已被占用");
        }

        Set<Long> orgIds = new HashSet<>(command.getOrganizationIds());
        Set<Long> allowedAppIds = organizationAppRepository.findAppIdsByOrganizationIds(orgIds);
        if (allowedAppIds.isEmpty()) {
            throw new BusinessException("关联角色不匹配组织应用");
        }
        for (RoleSelectionCommand selection : command.getRoleSelections()) {
            if (selection.getAppId() == null || selection.getRoleCode() == null || selection.getRoleCode().trim().isEmpty()) {
                throw new BusinessException("关联角色不完整");
            }
            if (!allowedAppIds.contains(selection.getAppId())) {
                throw new BusinessException("关联角色不匹配组织应用");
            }
        }

        String initialPassword = passwordEncoder.generateInitialPassword();
        Long userId = idGenerator.nextId();
        Long primaryOrgId = command.getOrganizationIds().get(0);

        User user = new User();
        user.setId(userId);
        user.setUsername(command.getUsername());
        user.setName(command.getName());
        user.setPhone(command.getPhone());
        user.setEmail(command.getEmail());
        user.setPassword(passwordEncoder.encode(initialPassword));
        user.setInitialPasswordFlag(true);
        user.setFailedLoginAttempts(0);
        user.setStatus(UserStatusEnum.NORMAL);
        user.setPrimaryOrgId(primaryOrgId);
        user.setCreatedAt(Instant.now());
        user.setDeleted(false);

        transactionManager.doInTransaction(() -> {
            userRepository.save(user);
            for (Long orgId : command.getOrganizationIds()) {
                orgMembershipRepository.addMembership(orgId, userId);
            }
            List<RoleGrant> grants = new ArrayList<>();
            for (RoleSelectionCommand selection : command.getRoleSelections()) {
                Role role = roleRepository.findByCode(selection.getRoleCode());
                RoleGrant grant = new RoleGrant();
                grant.setId(idGenerator.nextId());
                grant.setOrganizationId(primaryOrgId);
                grant.setUserId(userId);
                grant.setAppId(selection.getAppId());
                grant.setRoleId(role != null ? role.getId() : null);
                grant.setRoleCode(selection.getRoleCode());
                grant.setRoleCategory(RoleCategoryEnum.APPLICATION);
                grant.setCreatedAt(Instant.now());
                grant.setDeleted(false);
                grants.add(grant);
            }
            roleGrantRepository.saveAll(grants);
        });

        emailService.sendInitialPassword(command.getEmail(), initialPassword);
        return userId;
    }

    @Override
    public UserDetailResult getUserDetail(Long userId) {
        User user = requireUser(userId);
        List<Long> orgIds = orgMembershipRepository.listOrganizationIdsByUserId(userId);
        List<RoleGrant> grants = new ArrayList<>();
        for (Long orgId : orgIds) {
            grants.addAll(roleGrantRepository.listByUserIdAndOrganizationId(userId, orgId));
        }
        List<RoleSelectionResult> roleSelections = grants.stream()
                .map(g -> {
                    RoleSelectionResult r = new RoleSelectionResult();
                    r.setAppId(g.getAppId());
                    r.setRoleCode(g.getRoleCode());
                    return r;
                })
                .toList();

        UserDetailResult result = new UserDetailResult();
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setName(user.getName());
        result.setPhone(user.getPhone());
        result.setEmail(user.getEmail());
        result.setStatus(user.getStatus() == null ? null : user.getStatus().name());
        result.setCreatedAt(user.getCreatedAt());
        result.setOrganizationIds(orgIds);
        result.setRoleSelections(roleSelections);
        return result;
    }

    @Override
    public void updateUser(UpdateUserCommand command) {
        User user = requireUser(command.getUserId());
        ValidationUtils.requireNonBlank(command.getEmail(), "邮箱不能为空");
        ValidationUtils.requireEmailFormat(command.getEmail(), "邮箱格式不正确");
        ValidationUtils.requirePhoneFormat(command.getPhone(), "手机号格式不正确");

        User existingByEmail = userRepository.findByEmail(command.getEmail());
        if (existingByEmail != null && !existingByEmail.getId().equals(user.getId())) {
            throw new BusinessException("邮箱已被占用");
        }

        List<Long> currentOrgIds = orgMembershipRepository.listOrganizationIdsByUserId(user.getId());
        Set<Long> newOrgIds = command.getOrganizationIds() == null ? new HashSet<>() : new HashSet<>(command.getOrganizationIds());
        Set<Long> removedOrgIds = currentOrgIds.stream()
                .filter(orgId -> !newOrgIds.contains(orgId))
                .collect(Collectors.toSet());

        transactionManager.doInTransaction(() -> {
            user.setName(command.getName());
            user.setPhone(command.getPhone());
            user.setEmail(command.getEmail());
            userRepository.update(user);

            for (Long removedOrgId : removedOrgIds) {
                orgMembershipRepository.softDeleteByOrganizationIdAndUserId(removedOrgId, user.getId());
                roleGrantRepository.softDeleteByUserIdAndOrganizationId(user.getId(), removedOrgId);
            }

            for (Long newOrgId : newOrgIds) {
                if (!currentOrgIds.contains(newOrgId)) {
                    orgMembershipRepository.addMembership(newOrgId, user.getId());
                }
            }

            if (command.getRoleSelections() != null) {
                for (Long orgId : newOrgIds) {
                    roleGrantRepository.softDeleteByUserIdAndOrganizationId(user.getId(), orgId);
                }
                List<RoleGrant> grants = new ArrayList<>();
                Long primaryOrgId = user.getPrimaryOrgId() != null ? user.getPrimaryOrgId() : (newOrgIds.isEmpty() ? null : newOrgIds.iterator().next());
                for (RoleSelectionCommand selection : command.getRoleSelections()) {
                    Role role = roleRepository.findByCode(selection.getRoleCode());
                    RoleGrant grant = new RoleGrant();
                    grant.setId(idGenerator.nextId());
                    grant.setOrganizationId(primaryOrgId);
                    grant.setUserId(user.getId());
                    grant.setAppId(selection.getAppId());
                    grant.setRoleId(role != null ? role.getId() : null);
                    grant.setRoleCode(selection.getRoleCode());
                    grant.setRoleCategory(RoleCategoryEnum.APPLICATION);
                    grant.setCreatedAt(Instant.now());
                    grant.setDeleted(false);
                    grants.add(grant);
                }
                roleGrantRepository.saveAll(grants);
            }
        });
    }

    @Override
    public void toggleUserStatus(Long userId, String status) {
        User user = requireUser(userId);
        UserStatusEnum newStatus = UserStatusEnum.fromValue(status);
        if (newStatus == null) {
            throw new BusinessException("无效的状态值");
        }
        user.setStatus(newStatus);
        userRepository.update(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = requireUser(userId);
        transactionManager.doInTransaction(() -> {
            user.setDeleted(true);
            userRepository.update(user);
            List<Long> orgIds = orgMembershipRepository.listOrganizationIdsByUserId(userId);
            for (Long orgId : orgIds) {
                orgMembershipRepository.softDeleteByOrganizationIdAndUserId(orgId, userId);
                roleGrantRepository.softDeleteByUserIdAndOrganizationId(userId, orgId);
            }
        });
    }

    private User requireUser(Long userId) {
        if (userId == null) {
            throw new BusinessException("用户不存在");
        }
        User user = userRepository.findById(userId);
        if (user == null || user.isDeleted()) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private UserListItemResult toListItem(User user) {
        UserListItemResult item = new UserListItemResult();
        item.setId(user.getId());
        item.setUsername(user.getUsername());
        item.setName(user.getName());
        item.setPhone(user.getPhone());
        item.setEmail(user.getEmail());
        item.setStatus(user.getStatus() == null ? null : user.getStatus().name());
        item.setCreatedAt(user.getCreatedAt());

        List<Long> orgIds = orgMembershipRepository.listOrganizationIdsByUserId(user.getId());
        List<String> orgNames = new ArrayList<>();
        for (Long orgId : orgIds) {
            Organization org = organizationRepository.findById(orgId);
            if (org != null && !org.isDeleted()) {
                orgNames.add(org.getName());
            }
        }
        item.setOrganizationNames(orgNames);

        List<String> roleNames = new ArrayList<>();
        for (Long orgId : orgIds) {
            List<RoleGrant> grants = roleGrantRepository.listByUserIdAndOrganizationId(user.getId(), orgId);
            for (RoleGrant grant : grants) {
                Role role = roleRepository.findByCode(grant.getRoleCode());
                if (role != null) {
                    roleNames.add(role.getRoleName());
                }
            }
        }
        item.setRoleNames(roleNames);
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
