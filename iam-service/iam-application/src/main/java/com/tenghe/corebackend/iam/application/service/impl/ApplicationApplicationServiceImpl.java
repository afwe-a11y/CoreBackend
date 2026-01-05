package com.tenghe.corebackend.iam.application.service.impl;

import com.tenghe.corebackend.iam.application.ApplicationApplicationService;
import com.tenghe.corebackend.iam.application.command.CreateApplicationCommand;
import com.tenghe.corebackend.iam.application.command.UpdateApplicationCommand;
import com.tenghe.corebackend.iam.application.exception.BusinessException;
import com.tenghe.corebackend.iam.application.service.result.ApplicationDetailResult;
import com.tenghe.corebackend.iam.application.service.result.ApplicationListItemResult;
import com.tenghe.corebackend.iam.application.service.result.PageResult;
import com.tenghe.corebackend.iam.application.validation.ValidationUtils;
import com.tenghe.corebackend.iam.interfaces.ports.*;
import com.tenghe.corebackend.iam.interfaces.portdata.ApplicationPortData;
import com.tenghe.corebackend.iam.model.enums.ApplicationStatusEnum;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationApplicationServiceImpl implements ApplicationApplicationService {
  private static final int DEFAULT_PAGE_SIZE = 10;

  private final ApplicationRepository applicationRepository;
  private final ApplicationPermissionRepository applicationPermissionRepository;
  private final RoleRepository roleRepository;
  private final RolePermissionRepository rolePermissionRepository;
  private final IdGenerator idGenerator;
  private final TransactionManager transactionManager;

  public ApplicationApplicationServiceImpl(
      ApplicationRepository applicationRepository,
      ApplicationPermissionRepository applicationPermissionRepository,
      RoleRepository roleRepository,
      RolePermissionRepository rolePermissionRepository,
      IdGenerator idGenerator,
      TransactionManager transactionManager) {
    this.applicationRepository = applicationRepository;
    this.applicationPermissionRepository = applicationPermissionRepository;
    this.roleRepository = roleRepository;
    this.rolePermissionRepository = rolePermissionRepository;
    this.idGenerator = idGenerator;
    this.transactionManager = transactionManager;
  }

  @Override
  public PageResult<ApplicationListItemResult> listApplications(String keyword, Integer page, Integer size) {
    int pageNumber = normalizePage(page);
    int pageSize = normalizeSize(size);
    List<ApplicationPortData> applications = new ArrayList<>(applicationRepository.listAll());
    if (keyword != null && !keyword.trim().isEmpty()) {
      String keywordValue = keyword.trim();
      applications = applications.stream()
          .filter(app -> matchesKeyword(app, keywordValue))
          .toList();
    }
    applications.sort(Comparator.comparing(ApplicationPortData::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()))
        .reversed());
    long total = applications.size();
    List<ApplicationPortData> paged = paginate(applications, pageNumber, pageSize);
    List<ApplicationListItemResult> items = paged.stream()
        .map(this::toListItem)
        .toList();
    return new PageResult<>(items, total, pageNumber, pageSize);
  }

  @Override
  public Long createApplication(CreateApplicationCommand command) {
    ValidationUtils.requireNonBlank(command.getAppName(), "应用名称不能为空");
    ValidationUtils.requireNonBlank(command.getAppCode(), "应用编码不能为空");
    ValidationUtils.requireListNotEmpty(command.getPermissionIds(), "包含权限不能为空");

    if (applicationRepository.findByCode(command.getAppCode()) != null) {
      throw new BusinessException("应用编码已被占用");
    }

    Long id = idGenerator.nextId();
    ApplicationPortData application = new ApplicationPortData();
    application.setId(id);
    application.setAppName(command.getAppName());
    application.setAppCode(command.getAppCode());
    application.setDescription(command.getDescription());
    application.setStatus(ApplicationStatusEnum.ENABLED);
    application.setCreatedAt(Instant.now());
    application.setDeleted(false);

    transactionManager.doInTransaction(() -> {
      applicationRepository.save(application);
      Set<Long> permissionIds = new HashSet<>(command.getPermissionIds());
      applicationPermissionRepository.replaceAppPermissions(id, permissionIds);
    });
    return id;
  }

  @Override
  public ApplicationDetailResult getApplicationDetail(Long appId) {
    ApplicationPortData application = requireApplication(appId);
    Set<Long> permissionIds = applicationPermissionRepository.findPermissionIdsByAppId(appId);
    ApplicationDetailResult result = new ApplicationDetailResult();
    result.setId(application.getId());
    result.setAppName(application.getAppName());
    result.setAppCode(application.getAppCode());
    result.setDescription(application.getDescription());
    result.setStatus(application.getStatus() == null ? null : application.getStatus().name());
    result.setCreatedAt(application.getCreatedAt());
    result.setPermissionIds(new ArrayList<>(permissionIds));
    return result;
  }

  @Override
  public void updateApplication(UpdateApplicationCommand command) {
    ApplicationPortData application = requireApplication(command.getAppId());
    ValidationUtils.requireNonBlank(command.getAppName(), "应用名称不能为空");

    ApplicationStatusEnum status = ApplicationStatusEnum.fromValue(command.getStatus());
    if (status == null) {
      throw new BusinessException("应用状态不能为空");
    }

    if (command.getPermissionIds() != null) {
      Set<Long> existingPermissions = applicationPermissionRepository.findPermissionIdsByAppId(command.getAppId());
      Set<Long> newPermissions = new HashSet<>(command.getPermissionIds());
      Set<Long> removedPermissions = existingPermissions.stream()
          .filter(p -> !newPermissions.contains(p))
          .collect(Collectors.toSet());

      if (!removedPermissions.isEmpty()) {
        for (Long permissionId : removedPermissions) {
          if (rolePermissionRepository.existsByPermissionIdAndAppId(permissionId, command.getAppId())) {
            throw new BusinessException("无法移除已分配给角色的权限");
          }
        }
      }
    }

    transactionManager.doInTransaction(() -> {
      application.setAppName(command.getAppName());
      application.setDescription(command.getDescription());
      application.setStatus(status);
      applicationRepository.update(application);

      if (command.getPermissionIds() != null) {
        Set<Long> permissionIds = new HashSet<>(command.getPermissionIds());
        applicationPermissionRepository.replaceAppPermissions(command.getAppId(), permissionIds);
      }
    });
  }

  @Override
  public void deleteApplication(Long appId) {
    ApplicationPortData application = requireApplication(appId);
    if (roleRepository.countByAppId(appId) > 0) {
      throw new BusinessException("应用下存在角色，无法删除");
    }
    transactionManager.doInTransaction(() -> {
      application.setDeleted(true);
      applicationRepository.update(application);
      applicationPermissionRepository.softDeleteByAppId(appId);
    });
  }

  private ApplicationPortData requireApplication(Long appId) {
    if (appId == null) {
      throw new BusinessException("应用不存在");
    }
    ApplicationPortData application = applicationRepository.findById(appId);
    if (application == null) {
      throw new BusinessException("应用不存在");
    }
    return application;
  }

  private boolean matchesKeyword(ApplicationPortData application, String keyword) {
    if (application.getAppName() != null && application.getAppName().contains(keyword)) {
      return true;
    }
    if (application.getAppCode() != null && application.getAppCode().contains(keyword)) {
      return true;
    }
    return false;
  }

  private ApplicationListItemResult toListItem(ApplicationPortData application) {
    ApplicationListItemResult item = new ApplicationListItemResult();
    item.setId(application.getId());
    item.setAppName(application.getAppName());
    item.setAppCode(application.getAppCode());
    item.setDescription(application.getDescription());
    item.setStatus(application.getStatus() == null ? null : application.getStatus().name());
    item.setCreatedAt(application.getCreatedAt());
    return item;
  }

  private List<ApplicationPortData> paginate(List<ApplicationPortData> applications, int page, int size) {
    if (applications.isEmpty()) {
      return Collections.emptyList();
    }
    int fromIndex = Math.max(0, (page - 1) * size);
    if (fromIndex >= applications.size()) {
      return Collections.emptyList();
    }
    int toIndex = Math.min(fromIndex + size, applications.size());
    return applications.subList(fromIndex, toIndex);
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
