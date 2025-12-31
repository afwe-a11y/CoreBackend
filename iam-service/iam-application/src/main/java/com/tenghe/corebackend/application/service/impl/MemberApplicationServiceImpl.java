package com.tenghe.corebackend.application.service.impl;

import com.tenghe.corebackend.application.MemberApplicationService;

import com.tenghe.corebackend.application.command.CreateInternalMemberCommand;
import com.tenghe.corebackend.application.command.LinkExternalMemberCommand;
import com.tenghe.corebackend.application.command.RoleSelectionCommand;
import com.tenghe.corebackend.application.command.UpdateInternalMemberCommand;
import com.tenghe.corebackend.application.exception.BusinessException;
import com.tenghe.corebackend.application.service.result.CreateInternalMemberResult;
import com.tenghe.corebackend.application.service.result.ExternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.InternalMemberListItemResult;
import com.tenghe.corebackend.application.service.result.PageResult;
import com.tenghe.corebackend.application.service.result.UserSummaryResult;
import com.tenghe.corebackend.application.validation.ValidationUtils;
import com.tenghe.corebackend.interfaces.ExternalMembershipRepositoryPort;
import com.tenghe.corebackend.interfaces.IdGeneratorPort;
import com.tenghe.corebackend.interfaces.OrgMembershipRepositoryPort;
import com.tenghe.corebackend.interfaces.OrganizationAppRepositoryPort;
import com.tenghe.corebackend.interfaces.OrganizationRepositoryPort;
import com.tenghe.corebackend.interfaces.RoleGrantRepositoryPort;
import com.tenghe.corebackend.interfaces.RoleRepositoryPort;
import com.tenghe.corebackend.interfaces.TransactionManagerPort;
import com.tenghe.corebackend.interfaces.UserRepositoryPort;
import com.tenghe.corebackend.model.Role;
import com.tenghe.corebackend.model.enums.AccountTypeEnum;
import com.tenghe.corebackend.model.ExternalMembership;
import com.tenghe.corebackend.model.Organization;
import com.tenghe.corebackend.model.enums.RoleCategoryEnum;
import com.tenghe.corebackend.model.RoleGrant;
import com.tenghe.corebackend.model.User;
import com.tenghe.corebackend.model.enums.UserStatusEnum;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MemberApplicationServiceImpl implements MemberApplicationService {
    private static final int DEFAULT_PAGE_SIZE = 10;

    private final OrganizationRepositoryPort organizationRepository;
    private final OrganizationAppRepositoryPort organizationAppRepository;
    private final UserRepositoryPort userRepository;
    private final OrgMembershipRepositoryPort orgMembershipRepository;
    private final ExternalMembershipRepositoryPort externalMembershipRepository;
    private final RoleGrantRepositoryPort roleGrantRepository;
    private final RoleRepositoryPort roleRepository;
    private final IdGeneratorPort idGenerator;
    private final TransactionManagerPort transactionManager;

    public MemberApplicationServiceImpl(
            OrganizationRepositoryPort organizationRepository,
            OrganizationAppRepositoryPort organizationAppRepository,
            UserRepositoryPort userRepository,
            OrgMembershipRepositoryPort orgMembershipRepository,
            ExternalMembershipRepositoryPort externalMembershipRepository,
            RoleGrantRepositoryPort roleGrantRepository,
            RoleRepositoryPort roleRepository,
            IdGeneratorPort idGenerator,
            TransactionManagerPort transactionManager) {
        this.organizationRepository = organizationRepository;
        this.organizationAppRepository = organizationAppRepository;
        this.userRepository = userRepository;
        this.orgMembershipRepository = orgMembershipRepository;
        this.externalMembershipRepository = externalMembershipRepository;
        this.roleGrantRepository = roleGrantRepository;
        this.roleRepository = roleRepository;
        this.idGenerator = idGenerator;
        this.transactionManager = transactionManager;
    }

    @Override
    public PageResult<InternalMemberListItemResult> listInternalMembers(Long organizationId, Integer page, Integer size) {
        requireOrganization(organizationId);
        List<Long> userIds = orgMembershipRepository.listUserIdsByOrganizationId(organizationId);
        if (userIds.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, normalizePage(page), normalizeSize(size));
        }
        List<User> users = userRepository.findByIds(new HashSet<>(userIds));
        users.sort(Comparator.comparing(User::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        long total = users.size();
        List<User> paged = paginate(users, normalizePage(page), normalizeSize(size));
        List<InternalMemberListItemResult> items = new ArrayList<>();
        for (User user : paged) {
            InternalMemberListItemResult item = new InternalMemberListItemResult();
            item.setUsername(user.getUsername());
            item.setPhone(user.getPhone());
            item.setEmail(user.getEmail());
            item.setStatus(user.getStatus() == null ? null : user.getStatus().name());
            List<String> roles = roleGrantRepository.listByUserIdAndOrganizationId(user.getId(), organizationId).stream()
                    .map(RoleGrant::getRoleCode)
                    .toList();
            item.setRoles(roles);
            items.add(item);
        }
        return new PageResult<>(items, total, normalizePage(page), normalizeSize(size));
    }

    public CreateInternalMemberResult createInternalMember(CreateInternalMemberCommand command) {
        Long organizationId = command.getOrganizationId();
        requireOrganization(organizationId);
        ValidationUtils.requireNonBlank(command.getUsername(), "用户名不能为空");
        ValidationUtils.validateUsername(command.getUsername(), "用户名格式不正确");
        ValidationUtils.requireMaxLength(command.getName(), 20, "姓名长度超限");
        ValidationUtils.requireNonBlank(command.getEmail(), "邮箱不能为空");
        ValidationUtils.requirePhoneFormat(command.getPhone(), "手机号格式不正确");
        ValidationUtils.requireEmailFormat(command.getEmail(), "邮箱格式不正确");
        ValidationUtils.requireListNotEmpty(command.getOrganizationIds(), "关联组织不能为空");
        ValidationUtils.requireListNotEmpty(command.getRoleSelections(), "关联角色不能为空");

        if (!command.getOrganizationIds().contains(organizationId)) {
            throw new BusinessException("关联组织必须包含当前组织");
        }

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
        Map<String, Role> roleByCode = new HashMap<>();
        for (RoleSelectionCommand selection : command.getRoleSelections()) {
            if (selection.getAppId() == null || selection.getRoleCode() == null || selection.getRoleCode().trim().isEmpty()) {
                throw new BusinessException("关联角色不完整");
            }
            if (!allowedAppIds.contains(selection.getAppId())) {
                throw new BusinessException("关联角色不匹配组织应用");
            }
            Role role = roleRepository.findByCode(selection.getRoleCode());
            if (role == null) {
                throw new BusinessException("关联角色不存在");
            }
            if (!selection.getAppId().equals(role.getAppId())) {
                throw new BusinessException("关联角色不匹配组织应用");
            }
            roleByCode.put(selection.getRoleCode(), role);
        }

        UserStatusEnum status = UserStatusEnum.fromValue(command.getStatus());
        if (status == null) {
            status = UserStatusEnum.NORMAL;
        }
        AccountTypeEnum accountTypeValue = AccountTypeEnum.fromValue(command.getAccountType());
        if (accountTypeValue == null) {
            accountTypeValue = AccountTypeEnum.MANAGEMENT;
        }
        final AccountTypeEnum accountType = accountTypeValue;
        Long userId = idGenerator.nextId();
        User user = new User();
        user.setId(userId);
        user.setUsername(command.getUsername());
        user.setName(command.getName());
        user.setPhone(command.getPhone());
        user.setEmail(command.getEmail());
        user.setStatus(status);
        user.setAccountType(accountType);
        user.setPrimaryOrgId(organizationId);
        user.setCreatedAt(Instant.now());
        user.setDeleted(false);

        transactionManager.doInTransaction(() -> {
            userRepository.save(user);
            for (Long orgId : command.getOrganizationIds()) {
                orgMembershipRepository.addMembership(orgId, userId);
            }
            List<RoleGrant> grants = new ArrayList<>();
            for (RoleSelectionCommand selection : command.getRoleSelections()) {
                Role role = roleByCode.get(selection.getRoleCode());
                RoleGrant grant = new RoleGrant();
                grant.setId(idGenerator.nextId());
                grant.setOrganizationId(organizationId);
                grant.setUserId(userId);
                grant.setAppId(selection.getAppId());
                grant.setRoleId(role != null ? role.getId() : null);
                grant.setRoleCode(selection.getRoleCode());
                grant.setRoleCategory(toRoleCategory(accountType));
                grant.setCreatedAt(Instant.now());
                grant.setDeleted(false);
                grants.add(grant);
            }
            roleGrantRepository.saveAll(grants);
        });

        CreateInternalMemberResult result = new CreateInternalMemberResult();
        result.setUsername(user.getUsername());
        result.setPhone(user.getPhone());
        return result;
    }

    @Override
    public void updateInternalMember(UpdateInternalMemberCommand command) {
        Long organizationId = command.getOrganizationId();
        requireOrganization(organizationId);
        if (!orgMembershipRepository.exists(organizationId, command.getUserId())) {
            throw new BusinessException("成员不存在");
        }
        User user = userRepository.findById(command.getUserId());
        if (user == null || user.isDeleted()) {
            throw new BusinessException("成员不存在");
        }
        ValidationUtils.requireMaxLength(command.getName(), 20, "姓名长度超限");
        ValidationUtils.requireNonBlank(command.getEmail(), "邮箱不能为空");
        ValidationUtils.requirePhoneFormat(command.getPhone(), "手机号格式不正确");
        ValidationUtils.requireEmailFormat(command.getEmail(), "邮箱格式不正确");
        User existingByEmail = userRepository.findByEmail(command.getEmail());
        if (existingByEmail != null && !existingByEmail.getId().equals(user.getId())) {
            throw new BusinessException("邮箱已被占用");
        }
        AccountTypeEnum accountType = AccountTypeEnum.fromValue(command.getAccountType());
        if (accountType == null) {
            throw new BusinessException("账号类型不能为空");
        }
        UserStatusEnum status = command.getStatus() == null ? user.getStatus() : UserStatusEnum.fromValue(command.getStatus());
        if (status == null) {
            throw new BusinessException("账号状态不能为空");
        }
        user.setName(command.getName());
        user.setPhone(command.getPhone());
        user.setEmail(command.getEmail());
        user.setStatus(status);
        user.setAccountType(accountType);
        userRepository.update(user);
        roleGrantRepository.updateRoleCategoryByUserAndOrganization(command.getUserId(), organizationId, toRoleCategory(accountType));
    }

    @Override
    public void disableInternalMember(Long organizationId, Long userId) {
        requireOrganization(organizationId);
        if (!orgMembershipRepository.exists(organizationId, userId)) {
            throw new BusinessException("成员不存在");
        }
        User user = userRepository.findById(userId);
        if (user == null || user.isDeleted()) {
            throw new BusinessException("成员不存在");
        }
        user.setStatus(UserStatusEnum.DISABLED);
        userRepository.update(user);
    }

    @Override
    public void deleteInternalMember(Long organizationId, Long userId) {
        requireOrganization(organizationId);
        if (!orgMembershipRepository.exists(organizationId, userId)) {
            throw new BusinessException("成员不存在");
        }
        List<Long> userOrgIds = orgMembershipRepository.listOrganizationIdsByUserId(userId);
        boolean onlyCurrentOrg = userOrgIds.size() == 1 && userOrgIds.contains(organizationId);
        transactionManager.doInTransaction(() -> {
            if (onlyCurrentOrg) {
                userRepository.softDeleteById(userId);
            }
            orgMembershipRepository.softDeleteByOrganizationIdAndUserId(organizationId, userId);
            roleGrantRepository.softDeleteByUserIdAndOrganizationId(userId, organizationId);
        });
    }

    @Override
    public PageResult<ExternalMemberListItemResult> listExternalMembers(Long organizationId, Integer page, Integer size) {
        requireOrganization(organizationId);
        List<ExternalMembership> memberships = externalMembershipRepository.listByOrganizationId(organizationId);
        if (memberships.isEmpty()) {
            return new PageResult<>(Collections.emptyList(), 0, normalizePage(page), normalizeSize(size));
        }
        memberships.sort(Comparator.comparing(ExternalMembership::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        long total = memberships.size();
        List<ExternalMembership> paged = paginate(memberships, normalizePage(page), normalizeSize(size));
        List<ExternalMemberListItemResult> items = new ArrayList<>();
        for (ExternalMembership membership : paged) {
            User user = userRepository.findById(membership.getUserId());
            if (user == null || user.isDeleted()) {
                continue;
            }
            Organization sourceOrg = membership.getSourceOrganizationId() == null ? null
                    : organizationRepository.findById(membership.getSourceOrganizationId());
            ExternalMemberListItemResult item = new ExternalMemberListItemResult();
            item.setUsername(user.getUsername());
            item.setPhone(user.getPhone());
            item.setEmail(user.getEmail());
            item.setSourceOrganizationName(sourceOrg == null ? null : sourceOrg.getName());
            items.add(item);
        }
        return new PageResult<>(items, total, normalizePage(page), normalizeSize(size));
    }

    @Override
    public List<UserSummaryResult> searchExternalCandidates(Long organizationId, String keyword) {
        requireOrganization(organizationId);
        List<User> users = userRepository.searchByKeyword(keyword);
        List<UserSummaryResult> results = new ArrayList<>();
        for (User user : users) {
            UserSummaryResult summary = new UserSummaryResult();
            summary.setId(user.getId());
            summary.setUsername(user.getUsername());
            summary.setName(user.getName());
            summary.setPhone(user.getPhone());
            summary.setEmail(user.getEmail());
            results.add(summary);
        }
        return results;
    }

    @Override
    public void linkExternalMember(LinkExternalMemberCommand command) {
        Long organizationId = command.getOrganizationId();
        requireOrganization(organizationId);
        User user = userRepository.findById(command.getUserId());
        if (user == null || user.isDeleted()) {
            throw new BusinessException("用户不存在");
        }
        if (Objects.equals(user.getPrimaryOrgId(), organizationId) || orgMembershipRepository.exists(organizationId, user.getId())) {
            throw new BusinessException("不可添加本组织成员");
        }
        if (externalMembershipRepository.exists(organizationId, user.getId())) {
            throw new BusinessException("该用户已关联外部成员");
        }
        ExternalMembership existingExternal = externalMembershipRepository.findActiveByUserId(user.getId());
        if (existingExternal != null) {
            Organization org = organizationRepository.findById(existingExternal.getOrganizationId());
            String orgName = org == null ? "" : org.getName();
            throw new BusinessException("该用户已是外部成员，所属组织：" + orgName);
        }
        if (user.getPrimaryOrgId() == null) {
            throw new BusinessException("用户归属组织未知");
        }
        externalMembershipRepository.addExternalMembership(organizationId, user.getId(), user.getPrimaryOrgId());
    }

    @Override
    public void unlinkExternalMember(Long organizationId, Long userId) {
        requireOrganization(organizationId);
        externalMembershipRepository.softDeleteByOrganizationIdAndUserId(organizationId, userId);
    }

    private Organization requireOrganization(Long organizationId) {
        if (organizationId == null) {
            throw new BusinessException("组织不存在");
        }
        Organization organization = organizationRepository.findById(organizationId);
        if (organization == null || organization.isDeleted()) {
            throw new BusinessException("组织不存在");
        }
        return organization;
    }

    private RoleCategoryEnum toRoleCategory(AccountTypeEnum accountType) {
        return accountType == AccountTypeEnum.APPLICATION ? RoleCategoryEnum.APPLICATION : RoleCategoryEnum.MANAGEMENT;
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
