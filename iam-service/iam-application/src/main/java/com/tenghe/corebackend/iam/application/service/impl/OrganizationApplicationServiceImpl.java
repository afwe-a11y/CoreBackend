package com.tenghe.corebackend.iam.application.service.impl;

import com.tenghe.corebackend.iam.application.OrganizationApplicationService;
import com.tenghe.corebackend.iam.application.command.AssignAdminCommand;
import com.tenghe.corebackend.iam.application.command.CreateOrganizationCommand;
import com.tenghe.corebackend.iam.application.command.UpdateOrganizationCommand;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import com.tenghe.corebackend.iam.application.service.result.*;
import com.tenghe.corebackend.iam.application.validation.ValidationUtils;
import com.tenghe.corebackend.iam.interfaces.ports.*;
import com.tenghe.corebackend.iam.interfaces.portdata.*;
import com.tenghe.corebackend.iam.model.constants.RoleConstants;
import com.tenghe.corebackend.iam.model.enums.OrganizationStatusEnum;
import com.tenghe.corebackend.iam.model.enums.RoleCategoryEnum;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrganizationApplicationServiceImpl implements OrganizationApplicationService {
  private static final int DEFAULT_PAGE_SIZE = 10;

  private final OrganizationRepository organizationRepository;
  private final OrganizationAppRepository organizationAppRepository;
  private final UserRepository userRepository;
  private final OrgMembershipRepository orgMembershipRepository;
  private final ExternalMembershipRepository externalMembershipRepository;
  private final RoleGrantRepository roleGrantRepository;
  private final RoleRepository roleRepository;
  private final IdGenerator idGenerator;
  private final TransactionManager transactionManager;

  public OrganizationApplicationServiceImpl(
      OrganizationRepository organizationRepository,
      OrganizationAppRepository organizationAppRepository,
      UserRepository userRepository,
      OrgMembershipRepository orgMembershipRepository,
      ExternalMembershipRepository externalMembershipRepository,
      RoleGrantRepository roleGrantRepository,
      RoleRepository roleRepository,
      IdGenerator idGenerator,
      TransactionManager transactionManager) {
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
    List<OrganizationPortData> organizations = new ArrayList<>(organizationRepository.listAll());
    if (keyword != null && !keyword.trim().isEmpty()) {
      String keywordValue = keyword.trim();
      organizations = organizations.stream()
          .filter(org -> matchesKeyword(org, keywordValue))
          .toList();
    }
    organizations.sort(Comparator.comparing(OrganizationPortData::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .reversed());
    long total = organizations.size();
    List<OrganizationPortData> paged = paginate(organizations, pageNumber, pageSize);
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

    OrganizationPortData existingByName = organizationRepository.findByName(command.getName());
    if (existingByName != null) {
      throw new BusinessException("该组织名称已被占用");
    }
    OrganizationPortData existingByCode = organizationRepository.findByCode(command.getCode());
    if (existingByCode != null) {
      throw new BusinessException("组织编码已被占用");
    }

    Long id = idGenerator.nextId();
    OrganizationPortData organization = new OrganizationPortData();
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
    OrganizationPortData organization = requireOrganization(organizationId);
    List<Long> appIds = organizationAppRepository.listByOrganizationId(organizationId).stream()
        .map(OrganizationAppPortData::getAppId)
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
    OrganizationPortData organization = requireOrganization(command.getOrganizationId());
    ValidationUtils.requireNonBlank(command.getName(), "组织名称不能为空");
    ValidationUtils.requireMaxLength(command.getName(), 50, "组织名称长度超限");
    ValidationUtils.requireMaxLength(command.getDescription(), 400, "组织描述长度超限");
    ValidationUtils.requirePhoneFormat(command.getContactPhone(), "联系人手机号格式不正确");
    ValidationUtils.requireEmailFormat(command.getContactEmail(), "联系人邮箱格式不正确");

    if (!Objects.equals(organization.getName(), command.getName())) {
      OrganizationPortData existingByName = organizationRepository.findByName(command.getName());
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
            .map(OrganizationAppPortData::getAppId)
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
    OrganizationPortData organization = requireOrganization(organizationId);
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
    OrganizationPortData organization = requireOrganization(organizationId);
    DeleteOrganizationInfoResult result = new DeleteOrganizationInfoResult();
    result.setName(organization.getName());
    result.setUserCount(orgMembershipRepository.countByOrganizationId(organizationId));
    return result;
  }

  @Override
  public List<UserSummaryResult> searchAdminCandidates(Long organizationId, String keyword) {
    requireOrganization(organizationId);
    List<UserPortData> users = userRepository.searchByKeyword(keyword);
    return users.stream()
        .map(this::toUserSummary)
        .toList();
  }

  @Override
  public void assignAdmin(AssignAdminCommand command) {
    OrganizationPortData organization = requireOrganization(command.getOrganizationId());
    UserPortData user = userRepository.findById(command.getUserId());
    if (user == null || user.isDeleted()) {
      throw new BusinessException("用户不存在");
    }
    ValidationUtils.validateAdminDisplay(user.getUsername(), "管理员名称不合法");
    RolePortData role = roleRepository.findByCode(RoleConstants.ORG_ADMIN_ROLE_CODE);
    if (role == null) {
      throw new BusinessException("组织管理员角色不存在");
    }
    RoleGrantPortData grant = new RoleGrantPortData();
    grant.setId(idGenerator.nextId());
    grant.setOrganizationId(organization.getId());
    grant.setUserId(user.getId());
    grant.setAppId(RoleConstants.SYSTEM_APP_ID);
    grant.setRoleId(role != null ? role.getId() : null);
    grant.setRoleCode(RoleConstants.ORG_ADMIN_ROLE_CODE);
    grant.setRoleCategory(RoleCategoryEnum.ADMIN);
    grant.setCreatedAt(Instant.now());
    grant.setDeleted(false);
    roleGrantRepository.saveAll(Collections.singletonList(grant));

    organization.setPrimaryAdminDisplay(user.getUsername());
    organizationRepository.update(organization);
  }

  private OrganizationListItemResult toListItem(OrganizationPortData organization) {
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

  private OrganizationPortData requireOrganization(Long organizationId) {
    if (organizationId == null) {
      throw new BusinessException("组织不存在");
    }
    OrganizationPortData organization = organizationRepository.findById(organizationId);
    if (organization == null || organization.isDeleted()) {
      throw new BusinessException("组织不存在");
    }
    return organization;
  }

  private boolean matchesKeyword(OrganizationPortData organization, String keyword) {
    if (organization.getName() != null && organization.getName().contains(keyword)) {
      return true;
    }
    if (organization.getId() != null && organization.getId().toString().contains(keyword)) {
      return true;
    }
    return false;
  }

  private List<OrganizationPortData> paginate(List<OrganizationPortData> organizations, int page, int size) {
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

  private UserSummaryResult toUserSummary(UserPortData user) {
    UserSummaryResult result = new UserSummaryResult();
    result.setId(user.getId());
    result.setUsername(user.getUsername());
    result.setName(user.getName());
    result.setPhone(user.getPhone());
    result.setEmail(user.getEmail());
    return result;
  }
}
