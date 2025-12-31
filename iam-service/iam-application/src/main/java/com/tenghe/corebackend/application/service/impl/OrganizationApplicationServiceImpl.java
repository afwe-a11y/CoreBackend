package com.tenghe.corebackend.application.service.impl;

import com.tenghe.corebackend.application.OrganizationApplicationService;
import com.tenghe.corebackend.application.command.AssignAdminCommand;
import com.tenghe.corebackend.application.command.CreateOrganizationCommand;
import com.tenghe.corebackend.application.command.UpdateOrganizationCommand;
import com.tenghe.corebackend.application.exception.BusinessException;
import com.tenghe.corebackend.application.service.result.DeleteOrganizationInfoResult;
import com.tenghe.corebackend.application.service.result.OrganizationDetailResult;
import com.tenghe.corebackend.application.service.result.OrganizationListItemResult;
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
import com.tenghe.corebackend.model.Organization;
import com.tenghe.corebackend.model.OrganizationApp;
import com.tenghe.corebackend.model.enums.OrganizationStatusEnum;
import com.tenghe.corebackend.model.enums.RoleCategoryEnum;
import com.tenghe.corebackend.model.RoleGrant;
import com.tenghe.corebackend.model.constants.RoleConstants;
import com.tenghe.corebackend.model.User;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class OrganizationApplicationServiceImpl implements OrganizationApplicationService {
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

    public OrganizationApplicationServiceImpl(
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
    public PageResult<OrganizationListItemResult> listOrganizations(String keyword, Integer page, Integer size) {
        int pageNumber = normalizePage(page);
        int pageSize = normalizeSize(size);
        List<Organization> organizations = new ArrayList<>(organizationRepository.listAll());
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordValue = keyword.trim();
            organizations = organizations.stream()
                    .filter(org -> matchesKeyword(org, keywordValue))
                    .toList();
        }
        organizations.sort(Comparator.comparing(Organization::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
                .reversed());
        long total = organizations.size();
        List<Organization> paged = paginate(organizations, pageNumber, pageSize);
        List<OrganizationListItemResult> items = paged.stream()
                .map(this::toListItem)
                .toList();
        return new PageResult<>(items, total, pageNumber, pageSize);
    }

    @Override
    public Long createOrganization(CreateOrganizationCommand command) {
        ValidationUtils.requireNonBlank(command.getName(), "组织名称不能为空");
        ValidationUtils.requireMaxLength(command.getName(), 50, "组织名称长度超限");
        ValidationUtils.requireNonBlank(command.getCode(), "组织编码不能为空");
        ValidationUtils.validateOrgCode(command.getCode(), "组织编码格式不正确");
        ValidationUtils.requireMaxLength(command.getDescription(), 200, "组织描述长度超限");

        Organization existingByName = organizationRepository.findByName(command.getName());
        if (existingByName != null) {
            throw new BusinessException("该组织名称已被占用");
        }
        Organization existingByCode = organizationRepository.findByCode(command.getCode());
        if (existingByCode != null) {
            throw new BusinessException("组织编码已被占用");
        }

        Long id = idGenerator.nextId();
        Organization organization = new Organization();
        organization.setId(id);
        organization.setName(command.getName());
        organization.setCode(command.getCode());
        organization.setDescription(command.getDescription());
        organization.setStatus(OrganizationStatusEnum.NORMAL);
        organization.setCreatedAt(Instant.now());
        organization.setDeleted(false);
        organizationRepository.save(organization);

        Set<Long> appIds = command.getAppIds() == null ? Collections.emptySet() : new HashSet<>(command.getAppIds());
        organizationAppRepository.replaceOrgApps(id, appIds);
        return id;
    }

    @Override
    public OrganizationDetailResult getOrganizationDetail(Long organizationId) {
        Organization organization = requireOrganization(organizationId);
        List<Long> appIds = organizationAppRepository.listByOrganizationId(organizationId).stream()
                .map(OrganizationApp::getAppId)
                .toList();
        OrganizationDetailResult result = new OrganizationDetailResult();
        result.setId(organization.getId());
        result.setName(organization.getName());
        result.setCreatedAt(organization.getCreatedAt());
        result.setDescription(organization.getDescription());
        result.setStatus(organization.getStatus().name());
        result.setAppIds(appIds);
        result.setContactName(organization.getContactName());
        result.setContactPhone(organization.getContactPhone());
        result.setContactEmail(organization.getContactEmail());
        return result;
    }

    @Override
    public void updateOrganization(UpdateOrganizationCommand command) {
        Organization organization = requireOrganization(command.getOrganizationId());
        ValidationUtils.requireNonBlank(command.getName(), "组织名称不能为空");
        ValidationUtils.requireMaxLength(command.getName(), 50, "组织名称长度超限");
        ValidationUtils.requireMaxLength(command.getDescription(), 400, "组织描述长度超限");
        ValidationUtils.requirePhoneFormat(command.getContactPhone(), "联系人手机号格式不正确");
        ValidationUtils.requireEmailFormat(command.getContactEmail(), "联系人邮箱格式不正确");

        if (!Objects.equals(organization.getName(), command.getName())) {
            Organization existingByName = organizationRepository.findByName(command.getName());
            if (existingByName != null && !existingByName.getId().equals(organization.getId())) {
                throw new BusinessException("该组织名称已被占用");
            }
        }

        OrganizationStatusEnum status = OrganizationStatusEnum.fromValue(command.getStatus());
        if (status == null) {
            throw new BusinessException("组织状态不能为空");
        }

        transactionManager.doInTransaction(() -> {
            organization.setName(command.getName());
            organization.setDescription(command.getDescription());
            organization.setStatus(status);
            organization.setContactName(command.getContactName());
            organization.setContactPhone(command.getContactPhone());
            organization.setContactEmail(command.getContactEmail());
            organizationRepository.update(organization);

            if (command.getAppIds() != null) {
                Set<Long> existingApps = organizationAppRepository.listByOrganizationId(organization.getId()).stream()
                        .map(OrganizationApp::getAppId)
                        .collect(Collectors.toSet());
                Set<Long> newApps = new HashSet<>(command.getAppIds());
                Set<Long> removed = existingApps.stream()
                        .filter(appId -> !newApps.contains(appId))
                        .collect(Collectors.toSet());
                organizationAppRepository.replaceOrgApps(organization.getId(), newApps);
                if (!removed.isEmpty()) {
                    roleGrantRepository.softDeleteByOrganizationIdAndAppIds(organization.getId(), removed);
                }
            }
        });
    }

    @Override
    public void deleteOrganization(Long organizationId) {
        Organization organization = requireOrganization(organizationId);
        transactionManager.doInTransaction(() -> {
            organization.setDeleted(true);
            organizationRepository.update(organization);
            organizationAppRepository.replaceOrgApps(organizationId, Collections.emptySet());

            List<Long> internalUserIds = orgMembershipRepository.listUserIdsByOrganizationId(organizationId);
            for (Long userId : internalUserIds) {
                List<Long> userOrgIds = orgMembershipRepository.listOrganizationIdsByUserId(userId);
                boolean onlyCurrentOrg = userOrgIds.size() == 1 && userOrgIds.contains(organizationId);
                if (onlyCurrentOrg) {
                    userRepository.softDeleteById(userId);
                }
                roleGrantRepository.softDeleteByUserIdAndOrganizationId(userId, organizationId);
            }
            orgMembershipRepository.softDeleteByOrganizationId(organizationId);
            externalMembershipRepository.softDeleteByOrganizationId(organizationId);
            roleGrantRepository.softDeleteByOrganizationId(organizationId);
        });
    }

    @Override
    public DeleteOrganizationInfoResult getDeleteInfo(Long organizationId) {
        Organization organization = requireOrganization(organizationId);
        DeleteOrganizationInfoResult result = new DeleteOrganizationInfoResult();
        result.setName(organization.getName());
        result.setUserCount(orgMembershipRepository.countByOrganizationId(organizationId));
        return result;
    }

    @Override
    public List<UserSummaryResult> searchAdminCandidates(Long organizationId, String keyword) {
        requireOrganization(organizationId);
        List<User> users = userRepository.searchByKeyword(keyword);
        return users.stream()
                .map(this::toUserSummary)
                .toList();
    }

    @Override
    public void assignAdmin(AssignAdminCommand command) {
        Organization organization = requireOrganization(command.getOrganizationId());
        User user = userRepository.findById(command.getUserId());
        if (user == null || user.isDeleted()) {
            throw new BusinessException("用户不存在");
        }
        ValidationUtils.validateAdminDisplay(user.getUsername(), "管理员名称不合法");
        Role role = roleRepository.findByCode(RoleConstants.ORG_ADMIN_ROLE_CODE);
        if (role == null) {
            throw new BusinessException("组织管理员角色不存在");
        }
        RoleGrant grant = new RoleGrant();
        grant.setId(idGenerator.nextId());
        grant.setOrganizationId(organization.getId());
        grant.setUserId(user.getId());
        grant.setAppId(RoleConstants.SYSTEM_APP_ID);
        grant.setRoleId(role != null ? role.getId() : null);
        grant.setRoleCode(RoleConstants.ORG_ADMIN_ROLE_CODE);
        grant.setRoleCategory(RoleCategoryEnum.MANAGEMENT);
        grant.setCreatedAt(Instant.now());
        grant.setDeleted(false);
        roleGrantRepository.saveAll(Collections.singletonList(grant));

        organization.setPrimaryAdminDisplay(user.getUsername());
        organizationRepository.update(organization);
    }

    private OrganizationListItemResult toListItem(Organization organization) {
        OrganizationListItemResult item = new OrganizationListItemResult();
        item.setId(organization.getId());
        item.setName(organization.getName());
        item.setInternalMemberCount(orgMembershipRepository.countByOrganizationId(organization.getId()));
        item.setExternalMemberCount(externalMembershipRepository.countByOrganizationId(organization.getId()));
        item.setPrimaryAdminDisplay(organization.getPrimaryAdminDisplay());
        item.setStatus(organization.getStatus() == null ? null : organization.getStatus().name());
        item.setCreatedAt(organization.getCreatedAt());
        return item;
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

    private boolean matchesKeyword(Organization organization, String keyword) {
        if (organization.getName() != null && organization.getName().contains(keyword)) {
            return true;
        }
        if (organization.getId() != null && organization.getId().toString().contains(keyword)) {
            return true;
        }
        return false;
    }

    private List<Organization> paginate(List<Organization> organizations, int page, int size) {
        if (organizations.isEmpty()) {
            return Collections.emptyList();
        }
        int fromIndex = Math.max(0, (page - 1) * size);
        if (fromIndex >= organizations.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(fromIndex + size, organizations.size());
        return organizations.subList(fromIndex, toIndex);
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

    private UserSummaryResult toUserSummary(User user) {
        UserSummaryResult result = new UserSummaryResult();
        result.setId(user.getId());
        result.setUsername(user.getUsername());
        result.setName(user.getName());
        result.setPhone(user.getPhone());
        result.setEmail(user.getEmail());
        return result;
    }
}
